package com.example;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconLoader {

     public static ImageIcon loadIcon( String resourcePath ) {
        try {
            ClassLoader classLoader = IconLoader.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream( resourcePath );

            if ( inputStream == null ) {
                throw new IllegalArgumentException( "Resource not found: " + resourcePath );
            }

            BufferedImage image = ImageIO.read( inputStream );
            return new ImageIcon( image );
        } 
        catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

}
