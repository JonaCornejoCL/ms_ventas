package cl.tipum.blinblineo.ms_ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VentaRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El monto total es obligatorio")
    @Min(value = 1, message = "El monto total debe ser mayor a cero")
    private Integer montoTotal;

    @NotBlank(message = "El folio de la boleta no puede estar vacío")
    private String folioBoleta;

    // @Valid asegura que también se revisen las reglas de los detalles internos
    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid
    private List<DetalleVentaRequestDTO> detalles;
}