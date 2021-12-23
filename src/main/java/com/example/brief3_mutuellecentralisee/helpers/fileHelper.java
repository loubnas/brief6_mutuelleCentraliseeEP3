package com.example.brief3_mutuellecentralisee.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class fileHelper {
    public static String ReadAllText(String path) throws IOException, URISyntaxException {

        File file = new File("C:/Users/admin/IdeaProjects/Briefs_2ème_Annee/brief6_mutuelleCentraliseeEP3/src/main/resources/codePays.json");
        FileInputStream fis = new FileInputStream(file); // ouvrir fichier et lire contenu
        byte[] data = new byte[(int) file.length()];   //read file into bytes[]
        fis.read(data);
        fis.close();

       return new String(data, "UTF-8");
    }
}






