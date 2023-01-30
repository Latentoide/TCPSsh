package org.example.Model;

import java.io.File;
import java.io.Serializable;

public class Mensaje implements Serializable {
    private File f;

    public Mensaje(File f) {
        this.f = f;
    }

    public File getF() {
        return f;
    }
}
