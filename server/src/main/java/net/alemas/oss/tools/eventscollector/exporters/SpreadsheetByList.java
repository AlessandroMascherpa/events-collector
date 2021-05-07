package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.io.LogInOutResponse;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;


/**
 * Exporter class in spreadsheet format.
 *
 * Created by MASCHERPA on 24/03/2021.
 */
public class SpreadsheetByList
{
    /* --- singleton --- */
    /**
     * this class;
     */
    private static SpreadsheetByList    instance;

    /**
     * text messages resource;
     */
    private ResourceBundle              resources   = ResourceBundle.getBundle( "messages/spreadsheet" );


    /* --- constructors --- */
    /**
     * the single instance of this class;
     *
     * @return this class;
     */
    public static synchronized SpreadsheetByList getInstance()
    {
        if ( instance == null )
        {
            instance = new SpreadsheetByList();
        }
        return
                instance;
    }

    private SpreadsheetByList()
    {
        /* none */
    }

    /* --- exporter methods --- */
    public Resource export( Flux< LogInOutResponse > list )
        throws
            IOException
    {
        /* --- create book and sheet --- */
        XSSFWorkbook    book    = new XSSFWorkbook();
        XSSFSheet       sheet   = book.createSheet
                (
                        WorkbookUtil.createSafeSheetName
                                (
                                        this.resources.getString( "sheet.name" )
                                )
                );

        /* --- cell styles --- */
        XSSFCreationHelper  helper  = book.getCreationHelper();
        XSSFCellStyle       style   = book.createCellStyle();
        style.setDataFormat
                (
                        helper
                                .createDataFormat()
                                .getFormat
                                        (
                                                this.resources.getString( "sheet.cell-format.date" )
                                        )
                );

        sheet.setColumnWidth( 0, this.columnWidthByCharsLen( 30 ) );
        sheet.setColumnWidth( 1, this.columnWidthByCharsLen( 30 ) );
        sheet.setColumnWidth( 2, this.columnWidthByCharsLen( 22 ) );
        sheet.setColumnWidth( 3, this.columnWidthByCharsLen( 22 ) );


        /* --- fill the sheet: columns header --- */
        XSSFRow row = sheet.createRow( 0 );

        this.setColumnHeader( row, 0, "sheet.column-header.username" );
        this.setColumnHeader( row, 1, "sheet.column-header.application" );
        this.setColumnHeader( row, 2, "sheet.column-header.date-log-in" );
        this.setColumnHeader( row, 3, "sheet.column-header.date-log-out" );

        /* --- fill the sheet: events --- */
        list
//---                .log()
                .subscribe
                        (
                                new EventsConsumer( sheet, style, 1 )
                        )
                ;

        /* --- write the spreadsheet --- */
        ByteArrayInOutStream stream = new ByteArrayInOutStream();
        book.write( stream );

        return
                new InputStreamResource
                        (
                                stream.getInputStream()
                        )
                ;
    }
    private int columnWidthByCharsLen( int len )
    {
        return
                ( len * 256 );
    }
    private void setColumnHeader
            (
                    XSSFRow row,
                    int     cell,
                    String  key
            )
    {
        row
                .createCell( cell, CellType.STRING )
                .setCellValue
                        (
                                this.resources.getString
                                        (
                                                key
                                        )
                        );
    }


    /* --- internal classes --- */
    protected static class EventsConsumer implements Subscriber< LogInOutResponse >
    {
        /* --- constants --- */
        private static final int    AMOUNT  = 100;

        /* --- properties --- */
        private XSSFSheet       sheet;
        private XSSFCellStyle   style;
        private int             line;
        private Subscription    subscription;
        private int             consumed;

        /* --- constructors --- */
        protected EventsConsumer
            (
                    XSSFSheet       theSheet,
                    XSSFCellStyle   theStyle,
                    int             start
            )
        {
            this.sheet  = theSheet;
            this.style  = theStyle;
            this.line   = start;
        }

        /* --- implemented methods --- */
        @Override
        public void onSubscribe( Subscription subscription )
        {
            this.subscription   = subscription;
            this.subscription.request( AMOUNT );
        }

        @Override
        public void onNext( LogInOutResponse response )
        {
            /* --- fill the sheet --- */
            XSSFRow row = sheet.createRow( this.line ++ );

            row.createCell( 0, CellType.STRING ).setCellValue( response.getUsername() );
            row.createCell( 1, CellType.STRING ).setCellValue( response.getApplication() );

            this.setCellDateTime( row, 2, response.getDateLoggedIn() );
            this.setCellDateTime( row, 3, response.getDateLoggedOut() );

            /* --- request more events --- */
            if ( ++ this.consumed  >= AMOUNT )
            {
                this.consumed = 0;
                this.subscription.request( AMOUNT );
            }
        }
        private void setCellDateTime
                (
                        XSSFRow         row,
                        int             index,
                        LocalDateTime   dateTime
                )
        {
            if ( dateTime != null )
            {
                XSSFCell cell = row.createCell( index );
                cell.setCellStyle( this.style );
                cell.setCellValue
                        (
                                Date
                                        .from
                                                (
                                                        dateTime
                                                                .atZone( ZoneId.systemDefault() )
                                                                .toInstant()
                                                )
                        );
            }
        }

        @Override
        public void onError( Throwable throwable )
        {
            /* do nothing */
        }

        @Override
        public void onComplete()
        {
            /* do nothing */
        }
    }

}
