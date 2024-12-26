/** 
 *  Copyright 1999-2002 Matthew Robinson and Pavel Vorobiev. 
 *  All Rights Reserved. 
 * 
 *  =================================================== 
 *  This program contains code from the book "Swing" 
 *  2nd Edition by Matthew Robinson and Pavel Vorobiev 
 *  http://www.spindoczine.com/sbe 
 *  =================================================== 
 * 
 *  The above paragraph must be included in full, unmodified 
 *  and completely intact in the beginning of any source code 
 *  file that references, copies or uses (in any way, shape 
 *  or form) code contained in this file. 
 */
package com.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.example.mdi.*;

public class LayeredPaneDemo1 extends JFrame {
    public LayeredPaneDemo1() {
        super( "Custom MDI: Part I" );
        setSize( 570, 400 );
        getContentPane().setBackground( new Color( 244, 232, 152 ) );

        getLayeredPane().setOpaque( true );

        InnerFrame1[] frames = new InnerFrame1[5];
        for ( int i = 0; i < 5; i++ ) {
            frames[i] = new InnerFrame1( "InnerFrame " + i );
            frames[i].setBounds( 50 + i * 20, 50 + i * 20, 200, 200 );
            getLayeredPane().add( frames[i] );
        }

        WindowListener l = new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        };

        Dimension dim = getToolkit().getScreenSize();
        setLocation( dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2 );

        ImageIcon image = IconLoader.loadIcon( "spiral.gif" );
        setIconImage( image.getImage() );
        addWindowListener( l );
        setVisible( true );
    }

    public static void main( String[] args ) {
        LayeredPaneDemo1 mainFrame = new LayeredPaneDemo1();
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setVisible( true );
        mainFrame.setLocationCenter();
    }

    public void setLocationCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = screenSize.width / 2;
        int frameHeight = screenSize.height / 2;
        this.setSize(frameWidth, frameHeight);

        int x = ( screenSize.width - frameWidth ) / 2;
        int y = ( screenSize.height - frameHeight ) / 2;
        this.setLocation( x, y );
    }
}
