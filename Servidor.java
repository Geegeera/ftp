/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author Diego & Geraldine
 */
public class Servidor {
    public static final String HOST = "localhost";
    public static final int PUERTO = 5555;

    public static void main(String[] args) {
        try {
            //Creación de socket y se inicializa variable para mantener a la escucha
            boolean escuchando = true;
            ServerSocket socketListener = new ServerSocket(PUERTO);
            
            System.out.println("Servidor iniciado.\n Esperando conexiones...");
            
            while(escuchando) {    
                //Se acepta conexión entrante
                Socket socketServicio = socketListener.accept();
                System.out.println("Cliente conectado");
                
                //Obtención de flujos de entrada y salida
                DataInputStream delClientePrimitivo = new DataInputStream( new BufferedInputStream( socketServicio.getInputStream() ) );
                DataOutputStream alClientePrimitivo = new DataOutputStream( new BufferedOutputStream( socketServicio.getOutputStream() ) );
                
                //Obtención de número de Archivos a recibir
                int numArch = delClientePrimitivo.readInt();
                System.out.println("Preparado para recibir " + numArch + " archivos.");
                
                //for para procesar cada uno de los archivos
                for (int i = 0; i < numArch; i++) {
                    //Se crea objeto Archivo del archivo actual
                    Archivo archivo = new Archivo(delClientePrimitivo.readUTF(), Long.parseLong(delClientePrimitivo.readUTF()),
                                                  delClientePrimitivo.readUTF());
                    
                    InputStream delCliente = socketServicio.getInputStream();
                    FileOutputStream archivoAEscribir = new FileOutputStream("ArchivosRecibidos" + System.getProperty("file.separator") 
                                                                             + archivo.getNombre() + archivo.getExtension());
                    
                    System.out.println("\nRecibiendo " + archivo.getTamaño() + " bytes de " + archivo.getNombre() + archivo.getExtension() + " ...");
                    
                    long unCiento = archivo.getTamaño() / 100;
                    int porcentaje = 1;
                    long completado = 0;
        
                    int tamBufer = 256;
                    int tamArch = archivo.getTamaño().intValue();
                    int longitud;
                    byte[] datos = new byte[tamBufer];
                    
                    //Recepción de archivo y cálculo de porcentaje recibido
                    while ( (tamArch > 0 ) && delCliente.read(datos, 0, longitud = Math.min(tamBufer, tamArch )) != -1 ){
                        archivoAEscribir.write(datos, 0, longitud);
                        if (completado > porcentaje * unCiento) {
                            System.out.println(porcentaje + "% recibido...");
                            porcentaje++;
                        }
                        tamArch -= tamBufer;
                        completado += tamBufer;
                    }
                    archivoAEscribir.close();
                    //Envío de mensaje OK para indicar éxito en la transferencia
                    alClientePrimitivo.writeUTF("OK");
                    alClientePrimitivo.flush();

                    System.out.println("100% Archivo " + archivo.getNombre() + archivo.getExtension() + " recibido.");
                }
                //Servidor queda a la escucha de nuevas conexiones
                System.out.println("\n\nEsperando conexiones...");
            }   
        } catch (IOException ex) {
            System.err.println("Error en el servidor: " + ex.getMessage());
        }   
    }
}
