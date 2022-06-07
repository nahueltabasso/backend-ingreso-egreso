package backend.app.service.impl;

import backend.app.models.dto.ReporteDTO;
import backend.app.models.entity.CompraDolar;
import backend.app.models.entity.IngresoEgreso;
import backend.app.security.models.entity.Usuario;
import backend.app.service.ReporteService;
import backend.app.utils.email.EmailService;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteServiceImpl.class);
    @Autowired
    private EmailService emailService;

    @Override
    public void generarReporteIngresoEgreso(ReporteDTO reporteDTO, Integer periodo) {
        logger.info("Ingresa a generarReporteIngresoEgreso()");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Usuario usuarioDTO = reporteDTO.getUsuario();
        try {
            Document pdfDocument = new Document();
            File file = File.createTempFile("reporte_" + new Date().getTime(), ".pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument,
                    new FileOutputStream(file.getPath()));
            pdfDocument.open();

            PdfPTable table = new PdfPTable(1);
            table.setSpacingAfter(20);
            PdfPCell cell = null;
            cell = new PdfPCell(new Phrase("Reporte Ingreso - Egreso"));
            cell.setBackgroundColor(new Color(184, 218, 255));
            cell.setPadding(8f);
            table.addCell(cell);
            table.addCell("Usuario: \t" + usuarioDTO.getApellido() + ", " + usuarioDTO.getNombre());
            table.addCell("Email: \t" + usuarioDTO.getEmail());
            String fechaFormat = sdf.format(new Date());
            table.addCell("Fecha Reporte: \t" + fechaFormat);
            pdfDocument.add(table);

            PdfPTable obsTable = new PdfPTable(1);
            obsTable.setSpacingAfter(20);
            obsTable.addCell("Listado de operaciones de Ingresos - Egresos en $");
            pdfDocument.add(obsTable);

            PdfPTable table1 = new PdfPTable(5);
            table1.setSpacingAfter(20);
            table1.addCell("Fecha");
            table1.addCell("Descripcion");
            table1.addCell("Ingreso");
            table1.addCell("Egreso");
            table1.addCell("Tipo");

            for (IngresoEgreso ie : reporteDTO.getIngresoEgresoList()) {
                fechaFormat = sdf.format(ie.getCreateAt());
                table1.addCell(fechaFormat);
                table1.addCell(ie.getDescripcion());
                if (ie.getTipo().equalsIgnoreCase(IngresoEgreso.INGRESO)) {
                    table1.addCell("$" + ie.getMonto());
                    table1.addCell("");
                } else {
                    table1.addCell("");
                    table1.addCell("$" + ie.getMonto());
                }
                table1.addCell(ie.getTipo());
            }
            pdfDocument.add(table1);

            PdfPTable obsTable1 = new PdfPTable(1);
            obsTable1.setSpacingAfter(20);
            obsTable1.addCell("Listado de operaciones en U$D");
            pdfDocument.add(obsTable1);

            PdfPTable table2 = new PdfPTable(6);
            table2.setSpacingAfter(20);
            table2.addCell("Fecha");
            table2.addCell("Dolar (U$D)");
            table2.addCell("Tipo Dolar");
            table2.addCell("Valor Dolar - Peso");
            table2.addCell("Pesos ($)");
            table2.addCell("Tipo Operacion");
            for (CompraDolar cd : reporteDTO.getCompraDolarList()) {
                fechaFormat = sdf.format(cd.getCreateAt());
                table2.addCell(fechaFormat);
                table2.addCell("U$D" + cd.getCantidadDolarCompra());
                table2.addCell(cd.getTipo());
                table2.addCell("$" + cd.getValorDolarPeso());
                table2.addCell("$" + cd.getTotalPesos());
                table2.addCell(cd.getTipoOperacion());
            }
            pdfDocument.add(table2);
            pdfDocument.close();

            String subject = "Reporte Ingreso - Egreso";
            String body = "Se ha actualizado el registro de Historico, se adjunta el reporte con el detalle" +
                    " de las operaciones realizadas en el periodo " + periodo;
            logger.info(subject);
            logger.info(body);
            emailService.sendEmailWithAttachments(usuarioDTO.getEmail(), file.getPath(), subject, body);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
