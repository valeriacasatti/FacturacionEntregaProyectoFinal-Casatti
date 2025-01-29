package com.coderhouse.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.coderhouse.dtos.VentaDTO;
import com.coderhouse.dtos.ProductoDTO;
import com.coderhouse.models.Cliente;
import com.coderhouse.models.Producto;
import com.coderhouse.models.Venta;
import com.coderhouse.models.VentaProducto;
import com.coderhouse.repositories.ClienteRepository;
import com.coderhouse.repositories.ProductoRepository;
import com.coderhouse.repositories.VentaProductoRepository;
import com.coderhouse.repositories.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

	@Autowired
	private VentaRepository ventaRepository;
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private VentaProductoRepository ventaProductoRepository;
	@Autowired
	private FechaService fechaService;

	// GET ALL VENTAS
	public List<VentaDTO> getAllVentas() {
		return ventaRepository.findAll().stream()
				.map(venta -> new VentaDTO(venta.getId(), venta.getCliente().getId(), venta.getCliente().getNombre(),
						venta.getCliente().getApellido(), venta.getFecha(),
						venta.getVentaProductos().stream()
								.map(vp -> new ProductoDTO(vp.getProducto().getId(), vp.getProducto().getNombre(),
										vp.getPrecioUnitario(), vp.getCantidad()))
								.toList(),
						venta.getTotal()))
				.toList();
	}

	// GET VENTA BY ID
	public VentaDTO getVentaById(Long id) {
		Venta venta = ventaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

		return new VentaDTO(venta.getId(), venta.getCliente().getId(), venta.getCliente().getNombre(),
				venta.getCliente().getApellido(), venta.getFecha(),
				venta.getVentaProductos().stream().map(vp -> new ProductoDTO(vp.getProducto().getId(), vp.getProducto().getNombre(),
						vp.getPrecioUnitario(), vp.getCantidad())).toList(),
				venta.getTotal());
	}

	// CREAR VENTA/COMPROBANTE
	@Transactional
	public VentaDTO newVenta(Long clienteId, List<Long> productosId, List<Integer> cantidad) {
		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

		List<VentaProducto> ventaProductos = new ArrayList<>();
		int total = 0;

		// Crear la venta
		Venta nuevaVenta = new Venta();
		nuevaVenta.setCliente(cliente);
		nuevaVenta.setVentaProductos(ventaProductos);
		nuevaVenta.setTotal(total);

		// Asignar fecha y guardar la venta primero
		fechaService.asignarFecha(nuevaVenta);
		Venta ventaGuardada = ventaRepository.save(nuevaVenta);

		// Procesar productos
		for (int i = 0; i < productosId.size(); i++) {
			Long productoId = productosId.get(i);
			Integer cantidades = cantidad.get(i);

			Producto producto = productoRepository.findById(productoId)
					.orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

			// Validar cantidad y stock
			if (cantidades <= 0 || producto.getStock() < cantidades) {
				throw new IllegalArgumentException("Stock insuficiente o cantidad no válida");
			}

			// Reducir stock dentro de una transacción
			producto.setStock(producto.getStock() - cantidades);
			productoRepository.save(producto);

			// Crear registro de venta producto
			VentaProducto ventaProducto = new VentaProducto();
			ventaProducto.setProducto(producto);
			ventaProducto.setCantidad(cantidades);
			ventaProducto.setPrecioUnitario(producto.getPrecio());
			ventaProductos.add(ventaProducto);
			
		    ventaProducto.setVenta(ventaGuardada);

			total += producto.getPrecio() * cantidades;
		}
		
		ventaGuardada.setTotal(total);
		ventaRepository.save(ventaGuardada);

		for (VentaProducto vp : ventaProductos) {
			vp.setVenta(ventaGuardada);
			ventaProductoRepository.save(vp);
		}

		// Retornar DTO con los detalles de la venta
		return new VentaDTO(ventaGuardada.getId(), cliente.getId(), cliente.getNombre(), cliente.getApellido(),
				ventaGuardada.getFecha(),
				ventaProductos.stream().map(vp -> new ProductoDTO(vp.getProducto().getId(),
						vp.getProducto().getNombre(), vp.getPrecioUnitario(), vp.getCantidad())).toList(),
				ventaGuardada.getTotal());
	}

	// ACTUALIZAR VENTA
	@Transactional
	public VentaDTO updateVentaById(Long id, VentaDTO dto) {
		Venta venta = ventaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

		// Actualizar cliente
		if (dto.getClienteId() != null) {
			Cliente cliente = clienteRepository.findById(dto.getClienteId())
					.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
			venta.setCliente(cliente);
		}

		// Restaurar stock de los productos antes de actualizar
		for (VentaProducto vp : venta.getVentaProductos()) {
			Producto producto = vp.getProducto();
			producto.setStock(producto.getStock() + vp.getCantidad());
			productoRepository.save(producto);
		}

		// Eliminar los 'VentaProducto' anteriores
		ventaProductoRepository.deleteAll(venta.getVentaProductos());
		venta.getVentaProductos().clear();

		// Validar y agregar los nuevos productos
		List<VentaProducto> nuevosVentaProductos = new ArrayList<>();
		int nuevoTotal = 0;

		for (ProductoDTO productoDTO : dto.getProductos()) {

			Producto producto = productoRepository.findById(productoDTO.getId())
					.orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

			if (productoDTO.getCantidad() <= 0) {
				throw new IllegalArgumentException("Debe proporcionar una cantidad mayor a 0");
			}

			if (producto.getStock() < productoDTO.getCantidad()) {
				throw new IllegalArgumentException("Stock insuficiente");
			}

			// Reducir el stock
			producto.setStock(producto.getStock() - productoDTO.getCantidad());
			productoRepository.save(producto);

			// Recalcular el total
			nuevoTotal += producto.getPrecio() * productoDTO.getCantidad();

			// Crear el nuevo 'VentaProducto'
			VentaProducto ventaProducto = new VentaProducto();
			ventaProducto.setProducto(producto);
			ventaProducto.setCantidad(productoDTO.getCantidad());
			ventaProducto.setPrecioUnitario(producto.getPrecio());
			ventaProducto.setVenta(venta);
			nuevosVentaProductos.add(ventaProducto);
		}

		venta.setVentaProductos(nuevosVentaProductos);
		venta.setTotal(nuevoTotal);

		// Guardar la venta actualizada
		Venta ventaActualizada = ventaRepository.save(venta);

		// Guardar los nuevos 'VentaProducto'
		for (VentaProducto vp : nuevosVentaProductos) {
			ventaProductoRepository.save(vp);
		}

		return new VentaDTO(ventaActualizada.getId(), ventaActualizada.getCliente().getId(),
				ventaActualizada.getCliente().getNombre(), ventaActualizada.getCliente().getApellido(),
				ventaActualizada.getFecha(),
				nuevosVentaProductos.stream().map(vp -> new ProductoDTO(vp.getProducto().getId(),
						vp.getProducto().getNombre(), vp.getPrecioUnitario(), vp.getCantidad())).toList(),
				ventaActualizada.getTotal());
	}

	// ELIMINAR VENTA
	public void deleteVentaById(Long id) {

		Venta venta = ventaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

		// Restaurar stock
		for (VentaProducto vp : venta.getVentaProductos()) {
			Producto producto = vp.getProducto();
			producto.setStock(producto.getStock() + vp.getCantidad());
			productoRepository.save(producto);
		}

		// Eliminar los VentaProducto relacionados
		ventaProductoRepository.deleteAll(venta.getVentaProductos());
		// Eliminar la venta
		ventaRepository.deleteById(id);
	}
}