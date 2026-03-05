package sistema_compras.SistemaCompras.service;

import sistema_compras.SistemaCompras.entity.Pago;
import sistema_compras.SistemaCompras.entity.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // ------------------ ENVIAR EMAIL GENÉRICO ------------------
    @Async
    public void enviarEmail(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);
            email.setFrom("noreply@tutienda.com");

            mailSender.send(email);
            logger.info("Email enviado a: {}", destinatario);
        } catch (Exception e) {
            logger.error("Error enviando email a {}: {}", destinatario, e.getMessage());
        }
    }

    // ------------------ ENVIAR CONFIRMACIÓN DE PEDIDO ------------------
    @Async
    public void enviarConfirmacionPedido(Pedido pedido) {
        String destinatario = pedido.getCliente().getEmail();
        String asunto = "Confirmación de Pedido - " + pedido.getNumeroPedido();

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola ").append(pedido.getCliente().getNombre()).append(",\n\n");
        mensaje.append("Tu pedido ha sido confirmado exitosamente.\n\n");
        mensaje.append("Número de pedido: ").append(pedido.getNumeroPedido()).append("\n");
        mensaje.append("Fecha: ").append(pedido.getFechaPedido()).append("\n");
        mensaje.append("Total: $").append(pedido.getTotal()).append("\n\n");
        mensaje.append("Dirección de envío: ").append(pedido.getDireccionEnvio()).append("\n\n");
        mensaje.append("Gracias por tu compra.\n\n");
        mensaje.append("Saludos,\n");
        mensaje.append("Equipo de Tu Tienda");

        enviarEmail(destinatario, asunto, mensaje.toString());
    }

    // ------------------ ENVIAR CONFIRMACIÓN DE PAGO ------------------
    @Async
    public void enviarConfirmacionPago(Pago pago) {
        String destinatario = pago.getPedido().getCliente().getEmail();
        String asunto = "Confirmación de Pago - " + pago.getCodigoTransaccion();

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola ").append(pago.getPedido().getCliente().getNombre()).append(",\n\n");
        mensaje.append("Tu pago ha sido procesado exitosamente.\n\n");
        mensaje.append("Código de transacción: ").append(pago.getCodigoTransaccion()).append("\n");
        mensaje.append("Monto: $").append(pago.getImporte()).append("\n");
        mensaje.append("Fecha: ").append(pago.getFechaPago()).append("\n");
        mensaje.append("Pedido: ").append(pago.getPedido().getNumeroPedido()).append("\n\n");
        mensaje.append("Gracias por tu compra.\n\n");
        mensaje.append("Saludos,\n");
        mensaje.append("Equipo de Tu Tienda");

        enviarEmail(destinatario, asunto, mensaje.toString());
    }

    // ------------------ ENVIAR ACTUALIZACIÓN DE ESTADO ------------------
    @Async
    public void enviarActualizacionEstado(Pedido pedido) {
        String destinatario = pedido.getCliente().getEmail();
        String asunto = "Actualización de tu Pedido - " + pedido.getNumeroPedido();

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola ").append(pedido.getCliente().getNombre()).append(",\n\n");
        mensaje.append("Tu pedido ha sido actualizado.\n\n");
        mensaje.append("Número de pedido: ").append(pedido.getNumeroPedido()).append("\n");
        mensaje.append("Estado actual: ").append(pedido.getEstado()).append("\n\n");

        switch (pedido.getEstado()) {
            case PROCESANDO:
                mensaje.append("Tu pedido está siendo procesado.\n");
                break;
            case ENVIADO:
                mensaje.append("Tu pedido ha sido enviado.\n");
                if (pedido.getFechaEnvio() != null) {
                    mensaje.append("Fecha de envío: ").append(pedido.getFechaEnvio()).append("\n");
                }
                break;
            case ENTREGADO:
                mensaje.append("Tu pedido ha sido entregado.\n");
                mensaje.append("¡Esperamos que disfrutes tu compra!\n");
                break;
            case CANCELADO:
                mensaje.append("Tu pedido ha sido cancelado.\n");
                break;
            default:
                break;
        }

        mensaje.append("\nGracias por tu compra.\n\n");
        mensaje.append("Saludos,\n");
        mensaje.append("Equipo de Tu Tienda");

        enviarEmail(destinatario, asunto, mensaje.toString());
    }

    // ------------------ ENVIAR RECUPERACIÓN DE CONTRASEÑA ------------------
    @Async
    public void enviarRecuperacionContrasena(String destinatario, String token) {
        String asunto = "Recuperación de Contraseña";

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola,\n\n");
        mensaje.append("Has solicitado recuperar tu contraseña.\n\n");
        mensaje.append("Tu código de recuperación es: ").append(token).append("\n\n");
        mensaje.append("Este código expira en 1 hora.\n\n");
        mensaje.append("Si no solicitaste este cambio, ignora este email.\n\n");
        mensaje.append("Saludos,\n");
        mensaje.append("Equipo de Tu Tienda");

        enviarEmail(destinatario, asunto, mensaje.toString());
    }

    // ------------------ ENVIAR BIENVENIDA ------------------
    @Async
    public void enviarBienvenida(String destinatario, String nombre) {
        String asunto = "¡Bienvenido a Tu Tienda!";

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola ").append(nombre).append(",\n\n");
        mensaje.append("¡Bienvenido a Tu Tienda!\n\n");
        mensaje.append("Estamos encantados de tenerte con nosotros.\n");
        mensaje.append("Puedes comenzar a explorar nuestros productos y realizar tu primera compra.\n\n");
        mensaje.append("Si tienes alguna pregunta, no dudes en contactarnos.\n\n");
        mensaje.append("Saludos,\n");
        mensaje.append("Equipo de Tu Tienda");

        enviarEmail(destinatario, asunto, mensaje.toString());
    }
}