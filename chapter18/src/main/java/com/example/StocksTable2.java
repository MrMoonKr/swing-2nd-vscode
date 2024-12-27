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
//import java.awt.event.*;
import java.util.*;
//import java.io.*;
import java.text.*;

import javax.swing.*;
import javax.swing.border.*;
//import javax.swing.event.*;
import javax.swing.table.*;

public class StocksTable2 extends JFrame {

    protected JTable m_table;
    protected StockTableData m_data;
    protected JLabel m_title;

    public StocksTable2() {
        super( "Stocks Table" );
        setSize( 600, 300 );

        UIManager.put( "Table.focusCellHighlightBorder", new LineBorder( Color.black, 0 ) );

        m_data = new StockTableData();

        m_title = new JLabel( m_data.getTitle(), IconLoader.loadIcon( "money.gif" ), SwingConstants.CENTER );
        m_title.setFont( new Font( "Helvetica", Font.PLAIN, 24 ) );
        getContentPane().add( m_title, BorderLayout.NORTH );

        m_table = new JTable();
        m_table.setAutoCreateColumnsFromModel( false );
        m_table.setModel( m_data );

        for ( int k = 0; k < m_data.getColumnCount(); k++ ) {
            DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
            renderer.setHorizontalAlignment( StockTableData.m_columns[k].m_alignment );
            TableColumn column = new TableColumn( k, StockTableData.m_columns[k].m_width, renderer, null );
            m_table.addColumn( column );
        }

        JTableHeader header = m_table.getTableHeader();
        header.setUpdateTableInRealTime( false );

        JScrollPane ps = new JScrollPane();
        ps.getViewport().setBackground( m_table.getBackground() );
        ps.getViewport().add( m_table );
        getContentPane().add( ps, BorderLayout.CENTER );
    }

    public static void main( String argv[] ) {
        StocksTable2 frame = new StocksTable2();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
}

class ColoredTableCellRenderer extends DefaultTableCellRenderer {

    public void setValue( Object value ) {
        if ( value instanceof ColorData ) {
            ColorData cvalue = ( ColorData )value;
            setForeground( cvalue.m_color );
            setText( cvalue.m_data.toString() );
        } else if ( value instanceof IconData ) {
            IconData ivalue = ( IconData )value;
            setIcon( ivalue.m_icon );
            setText( ivalue.m_data.toString() );
        } else
            super.setValue( value );
    }
}

class ColorData {

    public Color m_color;
    public Object m_data;
    public static Color GREEN = new Color( 0, 128, 0 );
    public static Color RED = Color.red;

    public ColorData( Color color, Object data ) {
        m_color = color;
        m_data = data;
    }

    public ColorData( Double data ) {
        m_color = data.doubleValue() >= 0 ? GREEN : RED;
        m_data = data;
    }

    public String toString() {
        return m_data.toString();
    }
}

class IconData {

    public ImageIcon m_icon;
    public Object m_data;

    public IconData( ImageIcon icon, Object data ) {
        m_icon = icon;
        m_data = data;
    }

    public String toString() {
        return m_data.toString();
    }
}

class StockData {

    public static ImageIcon ICON_UP = IconLoader.loadIcon( "ArrUp.gif" );
    public static ImageIcon ICON_DOWN = IconLoader.loadIcon( "ArrDown.gif" );
    public static ImageIcon ICON_BLANK = IconLoader.loadIcon( "blank.gif" );

    public IconData m_symbol;
    public String m_name;
    public Double m_last;
    public Double m_open;
    public ColorData m_change;
    public ColorData m_changePr;
    public Long m_volume;

    public StockData( String symbol, String name, double last, double open, double change, double changePr,
            long volume ) {
        m_symbol = new IconData( getIcon( change ), symbol );
        m_name = name;
        m_last = Double.valueOf( last );
        m_open = Double.valueOf( open );
        m_change = new ColorData( Double.valueOf( change ) );
        m_changePr = new ColorData( Double.valueOf( changePr ) );
        m_volume = Long.valueOf( volume );
    }

    public static ImageIcon getIcon( double change ) {
        return ( change > 0 ? ICON_UP : ( change < 0 ? ICON_DOWN : ICON_BLANK ) );
    }
}

class ColumnData {

    public String m_title;
    public int m_width;
    public int m_alignment;

    public ColumnData( String title, int width, int alignment ) {
        m_title = title;
        m_width = width;
        m_alignment = alignment;
    }
}

class StockTableData extends AbstractTableModel {

    static final public ColumnData m_columns[] = { 
            new ColumnData( "Symbol", 100, JLabel.LEFT ),
            new ColumnData( "Name", 160, JLabel.LEFT ),
            new ColumnData( "Last", 100, JLabel.RIGHT ),
            new ColumnData( "Open", 100, JLabel.RIGHT ),
            new ColumnData( "Change", 100, JLabel.RIGHT ),
            new ColumnData( "Change %", 100, JLabel.RIGHT ),
            new ColumnData( "Volume", 100, JLabel.RIGHT ) 
        };

    protected SimpleDateFormat m_frm;
    protected NumberFormat m_volumeFormat; // NEW
    protected Vector<StockData> m_vector;
    protected Date m_date;

    public StockTableData() {
        m_frm = new SimpleDateFormat( "MM/dd/yyyy" );
        m_volumeFormat = NumberFormat.getInstance(); // NEW
        m_volumeFormat.setGroupingUsed( true );
        m_volumeFormat.setMaximumFractionDigits( 0 );

        m_vector = new Vector<StockData>();
        setDefaultData();
    }

    public void setDefaultData() {
        try {
            m_date = m_frm.parse( "12/18/2004" );
        } catch ( java.text.ParseException ex ) {
            m_date = null;
        }

        m_vector.removeAllElements();
        m_vector.addElement( new StockData( "ORCL", "Oracle Corp.", 23.6875, 25.375, -1.6875, -6.42, 24976600 ) );
        m_vector.addElement( new StockData( "EGGS", "Egghead.com", 17.25, 17.4375, -0.1875, -1.43, 2146400 ) );
        m_vector.addElement( new StockData( "T", "AT&T", 65.1875, 66, -0.8125, -0.10, 554000 ) );
        m_vector.addElement( new StockData( "LU", "Lucent Technology", 64.625, 59.9375, 4.6875, 9.65, 29856300 ) );
        m_vector.addElement( new StockData( "FON", "Sprint", 104.5625, 106.375, -1.8125, -1.82, 1135100 ) );
        m_vector.addElement( new StockData( "ENML", "Enamelon Inc.", 4.875, 5, -0.125, 0, 35900 ) );
        m_vector.addElement( new StockData( "CPQ", "Compaq Computers", 30.875, 31.25, -0.375, -2.18, 11853900 ) );
        m_vector.addElement( new StockData( "MSFT", "Microsoft Corp.", 94.0625, 95.1875, -1.125, -0.92, 19836900 ) );
        m_vector.addElement( new StockData( "DELL", "Dell Computers", 46.1875, 44.5, 1.6875, 6.24, 47310000 ) );
        m_vector.addElement( new StockData( "SUNW", "Sun Microsystems", 140.625, 130.9375, 10, 10.625, 17734600 ) );
        m_vector.addElement( new StockData( "IBM", "Intl. Bus. Machines", 183, 183.125, -0.125, -0.51, 4371400 ) );
        m_vector.addElement( new StockData( "HWP", "Hewlett-Packard", 70, 71.0625, -1.4375, -2.01, 2410700 ) );
        m_vector.addElement( new StockData( "UIS", "Unisys Corp.", 28.25, 29, -0.75, -2.59, 2576200 ) );
        m_vector.addElement( new StockData( "SNE", "Sony Corp.", 96.1875, 95.625, 1.125, 1.18, 330600 ) );
        m_vector.addElement( new StockData( "NOVL", "Novell Inc.", 24.0625, 24.375, -0.3125, -3.02, 6047900 ) );
        m_vector.addElement( new StockData( "HIT", "Hitachi, Ltd.", 78.5, 77.625, 0.875, 1.12, 49400 ) );
    }

    public int getRowCount() {
        return m_vector == null ? 0 : m_vector.size();
    }

    public int getColumnCount() {
        return m_columns.length;
    }

    public String getColumnName( int column ) {
        return m_columns[column].m_title;
    }

    public boolean isCellEditable( int nRow, int nCol ) {
        return false;
    }

    public Object getValueAt( int nRow, int nCol ) {
        if ( nRow < 0 || nRow >= getRowCount() )
            return "";
        StockData row = ( StockData )m_vector.elementAt( nRow );
        switch ( nCol ) {
        case 0:
            return row.m_symbol;
        case 1:
            return row.m_name;
        case 2:
            return row.m_last;
        case 3:
            return row.m_open;
        case 4:
            return row.m_change;
        case 5:
            return row.m_changePr;
        case 6:
            return m_volumeFormat.format( row.m_volume );
        }
        return "";
    }

    public String getTitle() {
        if ( m_date == null )
            return "Stock Quotes";
        return "Stock Quotes at " + m_frm.format( m_date );
    }
}