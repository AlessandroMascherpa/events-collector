package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The class exports as spreadsheet the logs in/out events.
 *
 * Created by MASCHERPA on 13/05/2021.
 */
@Service
public class SpreadsheetLogins extends SpreadsheetByList< LogInOutResponse >
{
    /* --- constructors --- */
    @Autowired
    public SpreadsheetLogins( ServerConfiguration configuration )
    {
        super( configuration );
    }

    /* --- implemented methods --- */
    @Override
    protected void setColumnsWidths( XSSFSheet sheet )
    {
        sheet.setColumnWidth( 0, this.columnWidthByCharsLen( 30 ) );
        sheet.setColumnWidth( 1, this.columnWidthByCharsLen( 30 ) );
        sheet.setColumnWidth( 2, this.columnWidthByCharsLen( 22 ) );
        sheet.setColumnWidth( 3, this.columnWidthByCharsLen( 22 ) );
    }

    @Override
    protected void setColumnsHeaders( XSSFRow row )
    {
        this.setColumnHeader( row, 0, "sheet.column-header.application" );
        this.setColumnHeader( row, 1, "sheet.column-header.username" );
        this.setColumnHeader( row, 2, "sheet.column-header.date-log-in" );
        this.setColumnHeader( row, 3, "sheet.column-header.date-log-out" );
    }

    @Override
    protected void fillRow
            (
                    XSSFRow             row,
                    LogInOutResponse    response
            )
    {
        row.createCell( 0, CellType.STRING ).setCellValue( response.getApplication() );
        row.createCell( 1, CellType.STRING ).setCellValue( response.getUsername() );

        this.setCellDateTime( row, 2, response.getDateLoggedIn() );
        this.setCellDateTime( row, 3, response.getDateLoggedOut() );
    }

}
