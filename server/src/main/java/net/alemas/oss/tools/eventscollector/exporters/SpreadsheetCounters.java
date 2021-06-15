package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;



/**
 * The class exports as spreadsheet the events counters.
 *
 * Created by MASCHERPA on 13/05/2021.
 */
public class SpreadsheetCounters extends SpreadsheetByList< CounterResponse >
{
    /* --- singleton --- */
    /**
     * this class;
     */
    protected static SpreadsheetCounters  instance;

    /* --- constructors --- */
    /**
     * the single instance of this class;
     *
     * @return this class;
     */
    public static synchronized SpreadsheetCounters getInstance()
    {
        if ( instance == null )
        {
            instance = new SpreadsheetCounters();
        }
        return
                instance;
    }

    /* --- implemented methods --- */
    @Override
    protected void setColumnsWidths( XSSFSheet sheet )
    {
        sheet.setColumnWidth( 0, this.columnWidthByCharsLen( 30 ) );
        sheet.setColumnWidth( 1, this.columnWidthByCharsLen( 30 ) );
        sheet.setColumnWidth( 2, this.columnWidthByCharsLen( 30 ) );
    }

    @Override
    protected void setColumnsHeaders( XSSFRow row )
    {
        this.setColumnHeader( row, 0, "sheet.column-header.application" );
        this.setColumnHeader( row, 1, "sheet.column-header.event-id" );
        this.setColumnHeader( row, 2, "sheet.column-header.event-counter" );
    }

    @Override
    protected EventsConsumer< CounterResponse > getConsumer
            (
                    XSSFSheet       theSheet,
                    XSSFCellStyle   theStyle,
                    int             start
            )
    {
        return
                new CountersConsumer( theSheet, theStyle, start );
    }

    /* --- internal classes --- */
    protected static class CountersConsumer extends EventsConsumer< CounterResponse >
    {
        /* --- constructors --- */
        public CountersConsumer
                (
                        XSSFSheet       theSheet,
                        XSSFCellStyle   theStyle,
                        int             start
                )
        {
            super( theSheet, theStyle, start );
        }

        /* --- implemented methods --- */
        @Override
        protected void fillRow
                (
                        XSSFRow         row,
                        CounterResponse response
                )
        {
            row.createCell( 0, CellType.STRING ).setCellValue( response.getApplication() );
            row.createCell( 1, CellType.STRING  ).setCellValue( response.getId() );
            row.createCell( 2, CellType.NUMERIC ).setCellValue( response.getCounter() );
        }
    }

}
