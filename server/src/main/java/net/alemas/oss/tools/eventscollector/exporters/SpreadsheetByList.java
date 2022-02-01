package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Exporter class in spreadsheet format.
 *
 * Created by MASCHERPA on 24/03/2021.
 *
 * @param <Response>    the events class to use to fill the spreadsheet;
 */
public abstract class SpreadsheetByList< Response >
{
    /* --- properties --- */
    /**
     * text messages resource;
     */
    private ResourceBundle              resources   = ResourceBundle.getBundle( "messages/spreadsheet" );

    /**
     * server configuration properties;
     */
    private final ServerConfiguration   properties;


    /* --- constructors --- */
    protected SpreadsheetByList( ServerConfiguration configuration )
    {
        this.properties = configuration;
    }

    /* --- exporter methods --- */
    /**
     * exports the response class in spreadsheet format;
     *
     * @param list    list of responses to export;
     * @return the spreadsheet with responses;
     * @throws IOException
     */
    public Resource export( Flux< Response > list )
        throws
            IOException
    {
        /* --- create book and sheet --- */
        String          sheetName   = this.resources.getString( "sheet.name" );
        XSSFWorkbook    book        = new XSSFWorkbook();
        XSSFSheet       sheet       = book.createSheet
                (
                        WorkbookUtil.createSafeSheetName
                                (
                                        sheetName
                                )
                );

        /* --- workbook properties --- */
        POIXMLProperties                properties  = book.getProperties();
        POIXMLProperties.CoreProperties core        = properties.getCoreProperties();

        core.setCreator( this.properties.getFileNameSpreadsheet() );
        core.setCreated( Optional.of( new Date() ) );
        core.setTitle( sheetName );

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

        /* --- columns widths --- */
        this.setColumnsWidths( sheet );

        /* --- fill the sheet: columns header --- */
        XSSFRow row = sheet.createRow( 0 );

        this.setColumnsHeaders( row );

        /* --- fill the sheet: events --- */
        list
//---                .log()
                .subscribe
                        (
                                this.getConsumer( sheet, style, 1 )
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
    protected int columnWidthByCharsLen( int len )
    {
        return
                ( len * 256 );
    }
    protected void setColumnHeader
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

    /* --- abstract methods --- */
    /**
     * sets the columns widths;
     *
     * @param sheet    to sheet to configure;
     */
    protected abstract void setColumnsWidths( XSSFSheet sheet );

    /**
     * writes the column headers;
     * @param row    the row to fill with headers;
     */
    protected abstract void setColumnsHeaders( XSSFRow row );

    /**
     * gets the consumer class to read the events list;
     *
     * @param theSheet    the sheet where the events will be written;
     * @param theStyle    the cells style;
     * @param start       the starting row;
     * @return the events list consumer;
     */
    protected abstract EventsConsumer< Response > getConsumer
            (
                    XSSFSheet       theSheet,
                    XSSFCellStyle   theStyle,
                    int             start
            );

    /* --- internal classes --- */
    /**
     * Events list consumer class.
     *
     * @param <Response>    the events class to use to fill the spreadsheet;
     */
    protected static abstract class EventsConsumer< Response > implements Subscriber< Response >
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
        public void onNext( Response response )
        {
            /* --- fill the sheet --- */
            XSSFRow row = sheet.createRow( this.line ++ );

            this.fillRow( row, response );

            /* --- request more events --- */
            if ( ++ this.consumed  >= AMOUNT )
            {
                this.consumed = 0;
                this.subscription.request( AMOUNT );
            }
        }
        protected void setCellDateTime
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

        /* --- abstract methods --- */
        /**
         * with given event fills a single sheet row;
         *
         * @param row         the row to fill;
         * @param response    the event to fill the row;
         */
        protected abstract void fillRow( XSSFRow row, Response response );

    }

}
