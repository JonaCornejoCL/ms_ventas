package cl.tipum.blinblineo.ms_ventas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.tipum.blinblineo.ms_ventas.dto.VentaDTOMapper;
import cl.tipum.blinblineo.ms_ventas.dto.VentaRequestDTO;
import cl.tipum.blinblineo.ms_ventas.dto.VentaResponseDTO;
import cl.tipum.blinblineo.ms_ventas.exceptions.RecursoNoEncontradoException;
import cl.tipum.blinblineo.ms_ventas.exceptions.RecursoYaExisteException;
import cl.tipum.blinblineo.ms_ventas.model.DetalleVenta;
import cl.tipum.blinblineo.ms_ventas.model.Venta;
import cl.tipum.blinblineo.ms_ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {
    // Inyección de dependencias
    private final VentaRepository ventaRepository;
    private final VentaDTOMapper ventaDTOMapper; // El nuevo traductor

    public List<VentaResponseDTO> obtenerTodasLasVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        // Validación del profe: Evitar resp vacías
        if (ventas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron ventas registradas.");
        }

        return ventas.stream()
                .map(ventaDTOMapper::toDTO)
                .collect(Collectors.toList());
    }

    public VentaResponseDTO procesarVenta(VentaRequestDTO request) {
        // Validación del profe: Evitar duplicados
        if (ventaRepository.existsByFolioBoleta(request.getFolioBoleta())) {
            throw new RecursoYaExisteException("Ya existe una venta con el folio: " + request.getFolioBoleta());
        }

        Venta nuevaVenta = Venta.builder()
                .idCliente(request.getIdCliente())
                .fechaVenta(LocalDateTime.now())
                .montoTotal(request.getMontoTotal())
                .estado("COMPLETADO")
                .folioBoleta(request.getFolioBoleta())
                .build();

        List<DetalleVenta> detalles = request.getDetalles().stream()
                .map(d -> DetalleVenta.builder()
                        .venta(nuevaVenta)
                        .skuProducto(d.getSkuProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .build())
                .collect(Collectors.toList());

        nuevaVenta.setDetalles(detalles);
        
        // Guarda y devuelve traducido a DTO
        return ventaDTOMapper.toDTO(ventaRepository.save(nuevaVenta));
    }
}