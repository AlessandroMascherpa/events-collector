package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The class exports as spreadsheet the events timers.
 *
 * Created by MASCHERPA on 26/05/2021.
 */
@Service
public class SpreadsheetTimers extends SpreadsheetByList< TimingResponse >
{
    /* --- constructors --- */
    @Autowired
    public SpreadsheetTimers( ServerConfiguration configuration )
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
        sheet.setColumnWidth( 3, this.columnWidthByCharsLen( 15 ) );
        sheet.setColumnWidth( 4, this.columnWidthByCharsLen( 15 ) );
        sheet.setColumnWidth( 5, this.columnWidthByCharsLen( 15 ) );
    }

    @Override
    protected void setColumnsHeaders( XSSFRow row )
    {
        this.setColumnHeader( row, 0, "sheet.column-header.application" );
        this.setColumnHeader( row, 1, "sheet.column-header.event-id" );
        this.setColumnHeader( row, 2, "sheet.column-header.event-counter" );
        this.setColumnHeader( row, 3, "sheet.column-header.event-avg" );
        this.setColumnHeader( row, 4, "sheet.column-header.event-min" );
        this.setColumnHeader( row, 5, "sheet.column-header.event-max" );
    }

    @Override
    protected void fillRow
            (
                    XSSFRow         row,
                    TimingResponse  response
            )
    {
        row.createCell( 0, CellType.STRING ).setCellValue( response.getApplication() );
        row.createCell( 1, CellType.STRING  ).setCellValue( response.getId() );
        row.createCell( 2, CellType.NUMERIC ).setCellValue( response.getCounter() );
        row.createCell( 3, CellType.NUMERIC ).setCellValue( response.getAverage() );
        row.createCell( 4, CellType.NUMERIC ).setCellValue( response.getMin() );
        row.createCell( 5, CellType.NUMERIC ).setCellValue( response.getMax() );
    }

}
