package net.alemas.oss.tools.eventscollector.exporters;


import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

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
        implements
            Subscriber< Response >,
            Publisher< Resource >,
            Subscription
{
    /* --- logging --- */
    final private static Logger                     log         = LoggerFactory.getLogger( SpreadsheetByList.class );

    /* --- constants --- */
    private static final int                        AMOUNT      = 100;

    /* --- properties --- */
    /**
     * text messages resource;
     */
    private final   ResourceBundle                  resources   = ResourceBundle.getBundle( "messages/spreadsheet" );

    /**
     * server configuration properties;
     */
    private final   ServerConfiguration             properties;

    /**
     * the exporter subscriber;
     */
    private         Subscriber< ? super Resource >  subscriber;

    /* --- spreadsheet related properties --- */
    private         XSSFWorkbook                    book;
    private         XSSFSheet                       sheet;
    private         XSSFCellStyle                   style;
    private         int                             line;
    private         Subscription                    subscription;
    private         int                             consumed;
    private         ByteArrayInOutStream            stream;

    /* --- constructors --- */
    protected SpreadsheetByList( ServerConfiguration configuration )
    {
        this.properties = configuration;
    }

    /* --- implemented methods - subscriber (called by repository) --- */
    @Override
    public void onSubscribe( Subscription subscription )
    {
        /* --- subscriber --- */
        this.subscription   = subscription;

        /* --- build up the spreadsheet --- */
        this.buildWorkbook();
    }

    @Override
    public void onNext( Response response )
    {
        /* --- fill the sheet --- */
        XSSFRow row = this.sheet.createRow( this.line ++ );

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
        this.subscriber.onError( throwable );
    }

    @Override
    public void onComplete()
    {
        try
        {
            this.stream = new ByteArrayInOutStream();
            this.book.write( this.stream );
            this.stream.flush();
            this.stream.close();

            if ( this.subscriber != null )
            {
                this.subscriber.onNext
                        (
                                new InputStreamResource
                                        (
                                                this.stream.getInputStream()
                                        )
                        );
                this.subscriber.onComplete();
                this.subscriber = null;

                this.stream = null;
            }
        }
        catch ( IOException exception )
        {
            this.subscriber.onError( exception );
        }
    }

    /* --- implemented methods - publisher (call the downstream) --- */
    @Override
    public void subscribe( Subscriber<? super Resource> subscription )
    {
        this.subscriber = subscription;
        this.subscriber.onSubscribe( this );
    }

    /* --- implemented methods - subscription --- */
    @Override
    public void request( long n )
    {
        this.subscription.request( AMOUNT );

        if ( this.stream != null )
        {
            this.subscriber.onNext
                    (
                            new InputStreamResource
                                    (
                                            this.stream.getInputStream()
                                    )
                    );
            this.subscriber.onComplete();
            this.subscriber = null;
        }
    }

    @Override
    public void cancel()
    {
        /* do nothing */
    }

    /* --- spreadsheet methods --- */
    private void buildWorkbook()
    {
        /* --- create book and sheet --- */
        String  sheetName   = this.resources.getString( "sheet.name" );

        this.book   = new XSSFWorkbook();
        this.sheet  = book.createSheet
                (
                        WorkbookUtil.createSafeSheetName
                                (
                                        sheetName
                                )
                );
        this.line   = 0;

        /* --- workbook properties --- */
        POIXMLProperties                properties  = this.book.getProperties();
        POIXMLProperties.CoreProperties core        = properties.getCoreProperties();

        core.setCreator( this.properties.getFileNameSpreadsheet() );
        core.setCreated( Optional.of( new Date() ) );
        core.setTitle( sheetName );

        /* --- cell styles --- */
        XSSFCreationHelper  helper  = this.book.getCreationHelper();

        this.style   = this.book.createCellStyle();
        this.style.setDataFormat
                (
                        helper
                                .createDataFormat()
                                .getFormat
                                        (
                                                this.resources.getString( "sheet.cell-format.date" )
                                        )
                );

        /* --- columns widths --- */
        this.setColumnsWidths( this.sheet );

        /* --- fill the sheet: columns header --- */
        XSSFRow row = this.sheet.createRow( this.line ++ );

        this.setColumnsHeaders( row );
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
     * with given event fills a single sheet row;
     *
     * @param row         the row to fill;
     * @param response    the event to fill the row;
     */
    protected abstract void fillRow( XSSFRow row, Response response );

}
