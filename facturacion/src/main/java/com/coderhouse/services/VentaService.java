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
import com.coderhouse.repositories.ClienteRepository;
import com.coderhouse.repositories.ProductoRepository;
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
	private FechaService fechaService;
	
	// GET ALL VENTAS
	public List<VentaDTO> getAllVentas() {
	    return ventaRepository.findAll().stream()
	        .map(venta -> new VentaDTO(
	            venta.getId(),
	            venta.getCliente().getId(),
	            venta.getCliente().getNombre(),
	            venta.getCliente().getApellido(),
	            venta.getFecha(),
	            venta.getProductos().stream()
	                .map(producto -> new ProductoDTO(
	                		producto.getId(),
	                		producto.getNombre(),
	                		producto.getPrecio(),
	                		(int) venta.getProductos().stream()
	                		.filter(p -> p.getId().equals(producto.getId()))
	                		.count() // cantidad
	                		))
	                .toList(),
	               venta.getTotal()
	        ))
	        .toList();
	}
	
	// GET VENTA BY ID
	public VentaDTO getVentaById(Long id) {
	    Venta venta = ventaRepository.findById(id)
	        .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

	    return new VentaDTO(
	        venta.getId(),
	        venta.getCliente().getId(),
	        venta.getCliente().getNombre(),
	        venta.getCliente().getApellido(),
	        venta.getFecha(),
	        venta.getProductos().stream()
	            .map(producto -> new ProductoDTO(
	            		producto.getId(),
	            		producto.getNombre(),
	            		producto.getPrecio(),
	            		(int) venta.getProductos().stream()
                		.filter(p -> p.getId().equals(producto.getId()))
                		.count() // cantidad
                		))
	            .toList(),
		        venta.getTotal()
	    );
	}
	
	// CREAR VENTA/COMPROBANTE
	@Transactional
	public VentaDTO newVenta(Long clienteId, List<Long> productosId, List<Integer> cantidad) {
		// Validar que el cliente exista
	    Cliente cliente = clienteRepository.findById(clienteId)
	            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
	    
	    // Validar que los productos existan
	    if(productosId == null || productosId.isEmpty() || cantidad == null || cantidad.isEmpty()) {
	    	throw new IllegalArgumentException("Debe proporcionar una lista de productos y cantidades");
	    }
	    
	    // Validar que exista la cantidad solicitada
	    if(productosId.size() != cantidad.size()) {
	    	throw new IllegalArgumentException("Stock insuficiente para la cantidad de productos solicitados");
	    }
	    
	    List<Producto> productos = new ArrayList<>();
	    int total = 0;
	    
	    for(int i = 0; i < productosId.size(); i++) {
	    	Long productoId = productosId.get(i);
	    	Integer cantidades = cantidad.get(i);
	    	
	    	if(cantidades <= 0) {
		    	throw new IllegalArgumentException("Debe proporcionar una cantidad mayor a 0");
	    	}
	    	
	    	Producto producto = productoRepository.findById(productoId)
	    			.orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
	    	
	    	// Validar stock
	    	if(producto.getStock() < cantidades) {
	    		throw new IllegalArgumentException("Stock insuficiente");
	    	}
	    	
	    	// Reducir stock
	    	producto.setStock(producto.getStock() - cantidades);
	    	productoRepository.save(producto);
	    	productos.add(producto);
	    	
	    	// Calcular el total de la venta
		    total += producto.getPrecio() * cantidades;
	    }
	    
	    // Crear venta
	    Venta nuevaVenta = new Venta();
	    nuevaVenta.setCliente(cliente);
	    nuevaVenta.setProductos(productos);
	    nuevaVenta.setTotal(total);
	    
	    // Asignar fecha
	    fechaService.asignarFecha(nuevaVenta);
	    
	    // Guardar la venta
		Venta ventaGuardada = ventaRepository.save(nuevaVenta);
		
		return new VentaDTO(
			    ventaGuardada.getId(),
			    cliente.getId(),
			    cliente.getNombre(),
			    cliente.getApellido(),
			    ventaGuardada.getFecha(),
			    productos.stream()
			        .map(producto -> new ProductoDTO(producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad.get(productos.indexOf(producto))))
			        .toList(),
				ventaGuardada.getTotal()
			);
	}
	
	// ACTUALIZAR VENTA
	@Transactional
	public VentaDTO updateVentaById(Long id, VentaDTO dto) {
		Venta venta = ventaRepository.findById(id)
				.orElseThrow(()-> new IllegalArgumentException("Venta no encontrada"));
		
		// Actualizar cliente
	    if (dto.getClienteId() != null) {
	        Cliente cliente = clienteRepository.findById(dto.getClienteId())
	                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
	        venta.setCliente(cliente);
	    }
	    
	    // Actualizar productos
	    if (dto.getProductos() != null && !dto.getProductos().isEmpty()) {
	    	
	    	// Restaurar el stock
	        for (Producto producto : venta.getProductos()) {
	        	if (producto.getStock() == null) {
	                throw new IllegalArgumentException("Stock nulo para producto: " + producto.getId());
	            }
	            producto.setStock(producto.getStock() + 1);
	            productoRepository.save(producto);
	        }
	        
	        // Validar y agregar los nuevos productos
	        List<Producto> nuevosProductos = new ArrayList<>();
	        int nuevoTotal = 0;
	        
	        for (ProductoDTO productoDTO : dto.getProductos()) {
	        	
	            Producto producto = productoRepository.findById(productoDTO.getId())
	                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
	            
	            if(productoDTO.getCantidad() <= 0) {
			    	throw new IllegalArgumentException("Debe proporcionar una cantidad mayor a 0");
	            }
	            
	            if (producto.getStock() < productoDTO.getCantidad()) {
	                throw new IllegalArgumentException("Stock insuficiente");
	            }
	            
	            producto.setStock(producto.getStock() - productoDTO.getCantidad()); // Reducir el stock
	            productoRepository.save(producto);
	            nuevosProductos.add(producto);
	            
	            // Recalcular el total
		        nuevoTotal += producto.getPrecio() * productoDTO.getCantidad();
	        }

	        venta.getProductos().clear(); 
	        venta.getProductos().addAll(nuevosProductos);
	        venta.setTotal(nuevoTotal);
	    }
	    
	    // Guardar la venta actualizada
	    Venta ventaActualizada = ventaRepository.save(venta);
	    
	    return new VentaDTO(
	            ventaActualizada.getId(),
	            ventaActualizada.getCliente().getId(),
	            ventaActualizada.getCliente().getNombre(),
	            ventaActualizada.getCliente().getApellido(),
	            ventaActualizada.getFecha(),
	            ventaActualizada.getProductos().stream()
	                    .map(producto -> new ProductoDTO(
	                    		producto.getId(),
	                    		producto.getNombre(),
	                    		producto.getPrecio(),
	                    		dto.getProductos().stream()
	                    		.filter(p -> p.getId().equals(producto.getId()))
	                    		.findFirst()
	                    		.orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"))
	                    		.getCantidad()
	                    	))
	                    .toList(),
	            ventaActualizada.getTotal()
	    );
	}
	
	// ELIMINAR VENTA
	public void deleteVentaById(Long id) {
		
		Venta venta = ventaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
		
		// Restaurar stock
		for (Producto producto : venta.getProductos()) {
			int cantidad = (int) venta.getProductos().stream()
					.filter(p -> p.getId().equals(producto.getId()))
					.count();
			producto.setStock(producto.getStock() + cantidad);
			productoRepository.save(producto);
		}
		
		ventaRepository.deleteById(id);
	}
}