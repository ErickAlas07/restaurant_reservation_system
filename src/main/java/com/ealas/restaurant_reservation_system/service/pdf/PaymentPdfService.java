package com.ealas.restaurant_reservation_system.service.pdf;

import com.ealas.restaurant_reservation_system.dto.PaymentDto;
import com.ealas.restaurant_reservation_system.service.IPaymentService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PaymentPdfService {

    @Autowired
    private IPaymentService paymentService;

    public byte[] generatePdfForUser(Long userId) throws IOException {
        List<PaymentDto> payments = paymentService.findPaymentsByUserId(userId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Crear el documento en orientación horizontal (landscape)
            Document document = new Document(PageSize.A4.rotate()); // Rotar la página para formato apaisado
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agregar título
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Historial de Pagos", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Espacio en blanco

            // Crear una tabla de 6 columnas (ID, Método, Monto, Estado, Fecha, Hora)
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100); // Ocupa todo el ancho del documento
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Encabezado de la tabla
            String[] headers = {"UUID", "Método", "Monto", "Estado", "Fecha", "Hora", "Reserva ID"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, new Font(Font.HELVETICA, 10, Font.BOLD)));
                headerCell.setBorder(PdfPCell.NO_BORDER); // Sin bordes
                headerCell.setPadding(10);
                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            // Agregar filas con los datos de los pagos
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

            for (PaymentDto payment : payments) {
                // ID del pago
                PdfPCell idCell = new PdfPCell(new Paragraph(payment.getUuid()));
                idCell.setBorder(PdfPCell.NO_BORDER);
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                // Método de pago
                PdfPCell methodCell = new PdfPCell(new Paragraph(String.valueOf(payment.getPaymentMethod())));
                methodCell.setBorder(PdfPCell.NO_BORDER);
                methodCell.setPadding(5);
                methodCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(methodCell);

                // Monto total
                PdfPCell amountCell = new PdfPCell(new Paragraph("$ " + String.valueOf(payment.getTotalAmount())));
                amountCell.setBorder(PdfPCell.NO_BORDER);
                amountCell.setPadding(4);
                amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(amountCell);

                // Estado del pago
                PdfPCell statusCell = new PdfPCell(new Paragraph(String.valueOf(payment.getPaymentStatus())));
                statusCell.setBorder(PdfPCell.NO_BORDER);
                statusCell.setPadding(4);
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(statusCell);

                // Fecha de pago
                Date paymentDate = payment.getPaymentDate();
                PdfPCell dateCell = new PdfPCell(new Paragraph(dateFormatter.format(paymentDate)));
                dateCell.setBorder(PdfPCell.NO_BORDER);
                dateCell.setPadding(4);
                dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(dateCell);

                // Hora de pago
                PdfPCell timeCell = new PdfPCell(new Paragraph(timeFormatter.format(paymentDate)));
                timeCell.setBorder(PdfPCell.NO_BORDER);
                timeCell.setPadding(5);
                timeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(timeCell);

                // ID de la reserva
                PdfPCell reservationIdCell = new PdfPCell(new Paragraph(String.valueOf(payment.getReservationId())));
                reservationIdCell.setBorder(PdfPCell.NO_BORDER);
                reservationIdCell.setPadding(3);
                reservationIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(reservationIdCell);
            }

            document.add(table); // Añadir la tabla al documento

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error al crear el PDF", e);
        }
    }
}
