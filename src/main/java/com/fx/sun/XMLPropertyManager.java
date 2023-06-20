package com.fx.sun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Die Klasse {@link XMLPropertyManager} speichert und lädt die Einstellungen
 * des Programms in oder aus einer XML-Datei. Dabei kann auf alle public und
 * protected Methoden der Basisklasse zugegriffen werden.
 *
 * @author Patrick R. Scharnow
 */
public class XMLPropertyManager extends Properties {

    /**
     * Serialisierungs-ID der Klasse {@link XMLPropertyManager}
     */
    private static final long serialVersionUID = 1L;
    /**
     * Absoluter Pfad der XML-Datei.
     */
    private String _path = null;

    /**
     * Erweiterter Konstruktor der Klasse {@link XMLPropertyManager}. Der
     * Konstruktor ruft die Methode load() auf und liest somit alle in der
     * XML-Datei gespeicherten Key und Values aus.
     *
     * @param path Absoluter Pfad der XML-Datei.
     */
    public XMLPropertyManager(String path) {
        _path = path;
        load();
    }

    /**
     * Lädt die Inhalte (Einstellungen) in der XML-Datei. Ist eine Datei noch
     * nicht vorhanden, dann wird sie durch Aufruf der Methode save() angelegt.
     */
    private void load() {
        FileInputStream fis = null;
        try {
            File file = new File(_path);
            //Falls die XML-Datei noch nicht vorhanden ist,
            //wird sie angelegt.
            if (!file.exists()) {
                save();
            }
            fis = new FileInputStream(file);
            clear();
            loadFromXML(fis);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (InvalidPropertiesFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Durch diese Methode werden alle Key's und Values in die XML-Datei
     * geschrieben. Der Speicherpfad wurde vorher durch den Konstruktor
     * festgelegt.
     */
    public void save() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_path);
            storeToXML(fos, "Comment");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
