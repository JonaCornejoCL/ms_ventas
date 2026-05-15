package cl.tipum.blinblineo.ms_ventas.service;

import cl.tipum.blinblineo.ms_ventas.dto.VentaRequestDTO;
import cl.tipum.blinblineo.ms_ventas.model.DetalleVenta;
import cl.tipum.blinblineo.ms_ventas.model.Venta;
import cl.tipum.blinblineo.ms_ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {

    // Inyección de dependencias recomendada por la rúbrica (vía constructor con Lombok)
    private final VentaRepository ventaRepository;

    @Transactional
    public Venta procesarVenta(VentaRequestDTO request) {
        // 1. construcción de la cabecera de la venta
        Venta nuevaVenta = Venta.builder()
                .idCliente(request.getIdCliente())
                .fechaVenta(LocalDateTime.now())
                .montoTotal(request.getMontoTotal())
                .estado("COMPLETADO")
                .folioBoleta(request.getFolioBoleta())
                .build();

        // 2. mapeo de la lista dtos a entidades de DETALLE
        List<DetalleVenta> detalles = request.getDetalles().stream()
                .map(d -> DetalleVenta.builder()
                        .venta(nuevaVenta)
                        .skuProducto(d.getSkuProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .build())
                .collect(Collectors.toList());

        // 3. detllaes y guardar
        nuevaVenta.setDetalles(detalles);
        return ventaRepository.save(nuevaVenta);
    }
}