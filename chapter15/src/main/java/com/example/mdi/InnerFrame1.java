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

package com.example.mdi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
//import javax.swing.border.EmptyBorder;

import com.example.IconLoader;

public class InnerFrame1 extends JPanel {
    private static String IMAGE_DIR = "mdi" + java.io.File.separator;
    private static ImageIcon ICONIZE_BUTTON_ICON = IconLoader.loadIcon( IMAGE_DIR + "iconize.gif" );
    private static ImageIcon RESTORE_BUTTON_ICON = IconLoader.loadIcon( IMAGE_DIR + "restore.gif" );
    private static ImageIcon CLOSE_BUTTON_ICON = IconLoader.loadIcon( IMAGE_DIR + "close.gif" );
    private static ImageIcon PRESS_CLOSE_BUTTON_ICON = IconLoader.loadIcon( IMAGE_DIR + "pressclose.gif" );
    private static ImageIcon PRESS_RESTORE_BUTTON_ICON = IconLoader.loadIcon( IMAGE_DIR + "pressrestore.gif" );
    private static ImageIcon PRESS_ICONIZE_BUTTON_ICON = IconLoader.loadIcon( IMAGE_DIR + "pressiconize.gif" );
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;
    private static final int TITLE_BAR_HEIGHT = 25;
    private static Color TITLE_BAR_BG_COLOR = new Color( 108, 190, 116 );

    private String m_title;
    private JLabel m_titleLabel;

    private boolean m_iconified;

    private JPanel m_titlePanel;
    private JPanel m_contentPanel;
    private JPanel m_buttonPanel;
    private JPanel m_buttonWrapperPanel;

    private InnerFrameButton m_iconize;
    private InnerFrameButton m_close;

    public InnerFrame1( String title ) {
        m_title = title;
        setLayout( new BorderLayout() );
        createTitleBar();
        m_contentPanel = new JPanel();
        add( m_titlePanel, BorderLayout.NORTH );
        add( m_contentPanel, BorderLayout.CENTER );
    }

    public void toFront() {
        if ( getParent() instanceof JLayeredPane )
            ( ( JLayeredPane )getParent() ).moveToFront( this );
    }

    public void close() {
        if ( getParent() instanceof JLayeredPane ) {
            JLayeredPane jlp = ( JLayeredPane )getParent();
            jlp.remove( InnerFrame1.this );
            jlp.repaint();
        }
    }

    public void setIconified( boolean b ) {
        m_iconified = b;
        if ( b ) {
            setBounds( getX(), getY(), WIDTH, TITLE_BAR_HEIGHT );
            m_iconize.setIcon( RESTORE_BUTTON_ICON );
            m_iconize.setPressedIcon( PRESS_RESTORE_BUTTON_ICON );
        } else {
            setBounds( getX(), getY(), WIDTH, HEIGHT );
            m_iconize.setIcon( ICONIZE_BUTTON_ICON );
            m_iconize.setPressedIcon( PRESS_ICONIZE_BUTTON_ICON );
            revalidate();
        }
    }

    public boolean isIconified() {
        return m_iconified;
    }

    ////////////////////////////////////////////
    //////////////// Title Bar /////////////////
    ////////////////////////////////////////////

    // create the title bar m_titlePanel
    public void createTitleBar() {
        m_titlePanel = new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension( InnerFrame1.WIDTH, InnerFrame1.TITLE_BAR_HEIGHT );
            }
        };
        m_titlePanel.setLayout( new BorderLayout() );
        m_titlePanel.setOpaque( true );
        m_titlePanel.setBackground( TITLE_BAR_BG_COLOR );

        m_titleLabel = new JLabel( m_title );
        m_titleLabel.setForeground( Color.black );

        m_close = new InnerFrameButton( CLOSE_BUTTON_ICON );
        m_close.setPressedIcon( PRESS_CLOSE_BUTTON_ICON );
        m_close.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                InnerFrame1.this.close();
            }
        } );

        m_iconize = new InnerFrameButton( ICONIZE_BUTTON_ICON );
        m_iconize.setPressedIcon( PRESS_ICONIZE_BUTTON_ICON );
        m_iconize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                InnerFrame1.this.setIconified( !InnerFrame1.this.isIconified() );
            }
        } );

        m_buttonWrapperPanel = new JPanel();
        m_buttonWrapperPanel.setOpaque( false );
        m_buttonPanel = new JPanel( new GridLayout( 1, 2 ) );
        m_buttonPanel.setOpaque( false );
        m_buttonPanel.add( m_iconize );
        m_buttonPanel.add( m_close );
        m_buttonPanel.setAlignmentX( 0.5f );
        m_buttonPanel.setAlignmentY( 0.5f );
        m_buttonWrapperPanel.add( m_buttonPanel );

        m_titlePanel.add( m_titleLabel, BorderLayout.CENTER );
        m_titlePanel.add( m_buttonWrapperPanel, BorderLayout.EAST );

        InnerFrameTitleBarMouseAdapter iftbma = new InnerFrameTitleBarMouseAdapter( this );
        m_titlePanel.addMouseListener( iftbma );
        m_titlePanel.addMouseMotionListener( iftbma );
    }

    // title bar mouse adapter for frame dragging
    class InnerFrameTitleBarMouseAdapter extends MouseInputAdapter {
        InnerFrame1 m_if;
        int m_XDifference, m_YDifference;
        boolean m_dragging;

        public InnerFrameTitleBarMouseAdapter( InnerFrame1 inf ) {
            m_if = inf;
        }

        public void mouseDragged( MouseEvent e ) {
            if ( m_dragging )
                m_if.setLocation( e.getX() - m_XDifference + getX(), e.getY() - m_YDifference + getY() );
        }

        public void mousePressed( MouseEvent e ) {
            m_if.toFront();
            m_XDifference = e.getX();
            m_YDifference = e.getY();
            m_dragging = true;
        }

        public void mouseReleased( MouseEvent e ) {
            m_dragging = false;
        }
    }

    // custom button class for title bar
    class InnerFrameButton extends JButton {
        Dimension m_dim;

        public InnerFrameButton( ImageIcon ii ) {
            super( ii );
            m_dim = new Dimension( ii.getIconWidth(), ii.getIconHeight() );
            setOpaque( false );
            setContentAreaFilled( false );
            setBorder( null );
        }

        public Dimension getPreferredSize() {
            return m_dim;
        }

        public Dimension getMinimumSize() {
            return m_dim;
        }

        public Dimension getMaximumSize() {
            return m_dim;
        }
    }
}
