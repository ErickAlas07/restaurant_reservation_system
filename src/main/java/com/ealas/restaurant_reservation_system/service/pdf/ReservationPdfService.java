package com.ealas.restaurant_reservation_system.service.pdf;

import com.ealas.restaurant_reservation_system.dto.reservation.ReservationDto;
import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.ealas.restaurant_reservation_system.service.IReservationService;
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
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationPdfService {

    @Autowired
    private IReservationService reservationService;

    public byte[] generateReservationsReport(LocalDate startDate, LocalDate endDate) throws IOException {
        // Filtrar las reservas por el rango de fechas y solo las confirmadas
        List<ReservationDto> reservations = reservationService.findReservationsByDateRange(startDate, endDate);

        // Calcular los ingresos totales usando
        double totalRevenue = reservationService.calculateTotalRevenue(reservations);

        double totalEventRevenue = 0.0;
        double totalTableRevenue = 0.0;
        DecimalFormat currencyFormatter = new DecimalFormat("$ ###,###.##");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agregar título
            Font titleFont = new Font(Font.HELVETICA, 17, Font.BOLD);
            Paragraph title = new Paragraph("Reporte de Reservas", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // Encabezado de información general
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            document.add(new Paragraph("Rango de fechas: " + startDate + " - " + endDate));
            document.add(new Paragraph(" "));

            // Crear tabla de las reservas
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Encabezados de la tabla
            String[] headers = {"ID", "Tipo de Reserva", "Fecha", "Hora", "Personas", "Total Pagado", "Estado"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, new Font(Font.HELVETICA, 12, Font.BOLD)));
                headerCell.setBorder(PdfPCell.NO_BORDER);
                headerCell.setPadding(10);
                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            // Iterar sobre las reservas y llenar la tabla
            for (ReservationDto reservation : reservations) {
                if (reservation.getStatus() != StatusReservation.CONFIRMED) {
                    continue; // Saltar reservas no confirmadas
                }

                double reservationTotal = reservation.getTotalAmount();
                totalRevenue += reservationTotal;

                // Separar las ganancias por eventos y mesas
                if (reservation.getReservationType() == ReservationType.EVENT) {
                    totalEventRevenue += reservationTotal;
                } else {
                    totalTableRevenue += reservationTotal;
                }

                // ID de la reserva
                PdfPCell idCell = new PdfPCell(new Paragraph(reservation.getId().toString()));
                idCell.setBorder(PdfPCell.NO_BORDER);
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                // Tipo de reserva
                PdfPCell typeCell = new PdfPCell(new Paragraph(String.valueOf(reservation.getReservationType())));
                typeCell.setBorder(PdfPCell.NO_BORDER);
                typeCell.setPadding(5);
                typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(typeCell);

                // Fecha de la reserva
                Date sqlDate = Date.valueOf(reservation.getReservationDate());
                PdfPCell dateCell = new PdfPCell(new Paragraph(dateFormatter.format(sqlDate)));
                dateCell.setBorder(PdfPCell.NO_BORDER);
                dateCell.setPadding(5);
                dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(dateCell);

                // Hora de la reserva
                PdfPCell timeCell = new PdfPCell(new Paragraph(reservation.getReservationTime().toString()));
                timeCell.setBorder(PdfPCell.NO_BORDER);
                timeCell.setPadding(5);
                timeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(timeCell);

                // Número de personas
                PdfPCell peopleCell = new PdfPCell(new Paragraph(reservation.getPeople().toString()));
                peopleCell.setBorder(PdfPCell.NO_BORDER);
                peopleCell.setPadding(5);
                peopleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(peopleCell);

                // Precio total de la reserva
                PdfPCell totalCell = new PdfPCell(new Paragraph(currencyFormatter.format(reservationTotal)));
                totalCell.setBorder(PdfPCell.NO_BORDER);
                totalCell.setPadding(5);
                totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(totalCell);

                // Estado de la reserva
                PdfPCell statusCell = new PdfPCell(new Paragraph(String.valueOf(reservation.getStatus())));
                statusCell.setBorder(PdfPCell.NO_BORDER);
                statusCell.setPadding(5);
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(statusCell);
            }

            document.add(table); // Añadir la tabla al documento

            document.add(new Paragraph(" ")); // Espacio en blanco
            document.add(new Paragraph(" "));

            // Crear una tabla para el resumen de ganancias con una columna
            PdfPTable revenueTable = new PdfPTable(1); // 1 columna
            revenueTable.setWidthPercentage(100); // Ajustar al 100% del ancho del documento
            revenueTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            // Agregar el título a la tabla
            PdfPCell revenueTitleCell = new PdfPCell(new Paragraph("Resumen de Ganancias", titleFont));
            revenueTitleCell.setBorder(PdfPCell.NO_BORDER); // Sin borde
            revenueTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            revenueTitleCell.setPadding(10);
            revenueTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
            revenueTable.addCell(revenueTitleCell);

            // Agregar totales a la tabla
            PdfPCell eventRevenueCell = new PdfPCell(new Paragraph("Total Ganado por Eventos: " + currencyFormatter.format(totalEventRevenue)));
            eventRevenueCell.setBorder(PdfPCell.NO_BORDER);
            eventRevenueCell.setPadding(5);
            revenueTable.addCell(eventRevenueCell);

            PdfPCell tableRevenueCell = new PdfPCell(new Paragraph("Total Ganado por Mesas: " + currencyFormatter.format(totalTableRevenue)));
            tableRevenueCell.setBorder(PdfPCell.NO_BORDER);
            tableRevenueCell.setPadding(5);
            revenueTable.addCell(tableRevenueCell);

            PdfPCell totalRevenueCell = new PdfPCell(new Paragraph("Ganancia Total: " + currencyFormatter.format(totalRevenue)));
            totalRevenueCell.setBorder(PdfPCell.NO_BORDER);
            totalRevenueCell.setPadding(5);
            revenueTable.addCell(totalRevenueCell);

            // Añadir la tabla al documento
            document.add(revenueTable);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error al crear el PDF", e);
        }
    }
}
