/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp;

/**
 *
 * @author Diego & Geraldine
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class Cliente {

    public static void main(String[] args) {
        //Se crea cuadro de diálogo para seleccionar múltiples archivos
        JFileChooser selectrArch = new JFileChooser();
        selectrArch.setMultiSelectionEnabled(true);
        
        ArrayList<Archivo> archivosAEnviar = new ArrayList<>();
        ArrayList<String> rutas = new ArrayList<>();
        
        
        try {
            //Creación de socket y flujos de entrada y salida
            Socket cliente = new Socket(Servidor.HOST, Servidor.PUERTO);
            OutputStream alServidor;
            FileInputStream archivoALeer;
            DataOutputStream alServidorPrimitivos  = new DataOutputStream(new BufferedOutputStream( cliente.getOutputStream() ));
            DataInputStream delServidorPrimitivos = new DataInputStream(new BufferedInputStream( cliente.getInputStream() ));
            
            int val = selectrArch.showOpenDialog(null);
            
            //Cada archivo seleccionado se agrega a un ArrayList de objetos Archivo y cada ruta se agrega a un ArrayList de Strings
            if (val == JFileChooser.APPROVE_OPTION) {
                for (File arch : selectrArch.getSelectedFiles()) {
                    rutas.add( arch.getPath() );
                    String nombreArch = arch.getName();
                    archivosAEnviar.add( 
                            new Archivo(
                                nombreArch.substring(0, nombreArch.lastIndexOf('.')), 
                                arch.length(), 
                                nombreArch.substring(nombreArch.lastIndexOf('.'), nombreArch.length())
                            )
                    );
                    System.out.println(archivosAEnviar.get(archivosAEnviar.size() - 1).toString() + "\n");
                }
                
                //Se manda al servidor el número de archivos a enviar
                alServidorPrimitivos.writeInt(archivosAEnviar.size());
                alServidorPrimitivos.flush();

                //for para recorrer los archivos del ArrayList y enviarlos al servidor
                for (int i = 0; i < rutas.size(); i++) {
                    int tamArch = archivosAEnviar.get(i).getTamaño().intValue();
                    for (String metadato : archivosAEnviar.get(i).getMetadato()) {
                        //Se envía al servidor el nombre, tamaño y extensión (metadato) del archivo actual
                        alServidorPrimitivos.writeUTF(metadato);
                    }
                    alServidorPrimitivos.flush();
                    
                    System.out.println("\nEnviando " + tamArch + " bytes del archivo: " + archivosAEnviar.get(i).getNombre() + archivosAEnviar.get(i).getExtension() + " ...");
                    
                    long unPCiento = tamArch / 100;
                    int porcentaje = 1;
                    long completado = 0;
                    alServidor = cliente.getOutputStream();

                    archivoALeer = new FileInputStream( rutas.get(i) );
                    int tamBufer = 256;
                    int longitud;
                    byte[] datos = new byte[tamBufer];

                    //Cálculo de porcentaje de envío y envío de archivo actual
                    while( (tamArch > 0) && archivoALeer.read(datos, 0, longitud = Math.min(tamBufer, (int) tamArch)) != -1 ){                            
                        alServidor.write(datos, 0, longitud);
                        alServidor.flush();
                        if (completado > porcentaje * unPCiento) {
                            System.out.println(porcentaje + "% enviado...");
                            porcentaje++;
                        }
                        tamArch -= tamBufer;
                        completado += tamBufer;
                    }
                    archivoALeer.close();
                    
                    //Si el servidor envía mensaje de OK el archivo se transfirió exitosamente
                    if( delServidorPrimitivos.readUTF().equals("OK") ){
                        System.out.println("100% Archivo " + archivosAEnviar.get(i).getNombre() + archivosAEnviar.get(i).getExtension() + " enviado.");
                    }
                }
            }       
        //Si ocurré un error se imprime en consola
        }catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }        
    }
}

