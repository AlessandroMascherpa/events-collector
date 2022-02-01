package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The class exports as spreadsheet the events counters.
 *
 * Created by MASCHERPA on 13/05/2021.
 */
@Service
public class SpreadsheetCounters extends SpreadsheetByList< CounterResponse >
{
    /* --- constructors --- */
    @Autowired
    public SpreadsheetCounters( ServerConfiguration configuration )
    {
        super( configuration );
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
