package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.Payment;
import com.ealas.restaurant_reservation_system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReservationReminder(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("erick55gal@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendPaymentConfirmationEmail(User user, Payment payment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Confirmación de pago");
        message.setText("Querido/a " + user.getName() + " " + user.getLastname() + ",\n\n"
                + "Tu pago de " + payment.getTotalAmount() + " ha sido procesado exitosamente para tu reservación del "
                + payment.getReservation().getReservationDate() + ".\n\n"
                + "¡Gracias por tu preferencia!");

        mailSender.send(message);
    }
}
