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
//Clase Archivo con nombre, tamaño, extensión y metadatos
public class Archivo {
    private String nombre;
    private Long tamaño;
    private String extension;
    private String[] metadato = new String[3];

    public Archivo(String nombre, Long tamaño, String extension) {
        setNombre(nombre);
        setTamaño(tamaño);
        setExtension(extension);
        setMetadato();
    }
    
    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTamaño() {
        return tamaño;
    }

    private void setTamaño(Long tamaño) {
        this.tamaño = tamaño;
    }

    public String getExtension() {
        return extension;
    }

    private void setExtension(String extension) {
        this.extension = extension;
    }

    public String[] getMetadato(){
        return this.metadato;
    }
    
    private void setMetadato(){
        this.metadato [0] = this.nombre;
        this.metadato [1] = Long.toString(this.tamaño);
        this.metadato [2] = this.extension;
    }
    
    @Override
    public String toString() {
        return "Archivo{" + "nombre=" + nombre + ", tama\u00f1o=" + tamaño + ", extension=" + extension + '}';
    }
}
