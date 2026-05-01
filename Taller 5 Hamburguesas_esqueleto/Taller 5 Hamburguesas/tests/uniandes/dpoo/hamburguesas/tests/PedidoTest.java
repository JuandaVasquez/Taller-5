package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest {
	private ProductoMenu hamburguesa;
    private ProductoMenu papas;
    private ProductoMenu gaseosa;
    private Ingrediente  queso;
    private Ingrediente  tomate;
 
    // Escenario 1: pedido vacío
    private Pedido pedidoVacio;
 
    // Escenario 2: pedido con un producto
    private Pedido pedidoUnProducto;
 
    // Escenario 3: pedido con un combo
    private Pedido pedidoCombo;
 
    // Escenario 4: pedido con producto ajustado
    private Pedido pedidoAjustado;
 
    // Directorio temporal para guardar facturas en pruebas
    private File carpetaTemporal;
 
    @BeforeEach
    void setUp( ) throws Exception
    {
        hamburguesa = new ProductoMenu( "hamburguesa sencilla", 14000 );
        papas       = new ProductoMenu( "papas medianas", 5500 );
        gaseosa     = new ProductoMenu( "gaseosa", 5000 );
        queso       = new Ingrediente( "queso mozzarella", 2500 );
        tomate      = new Ingrediente( "tomate", 1000 );
 
        // Escenario 1: vacío
        pedidoVacio = new Pedido( "Ana", "Calle 1" );
 
        // Escenario 2: un producto
        pedidoUnProducto = new Pedido( "Luis", "Calle 2" );
        pedidoUnProducto.agregarProducto( hamburguesa );
 
        // Escenario 3: con combo (10% descuento)
        ArrayList<ProductoMenu> items = new ArrayList<>( );
        items.add( hamburguesa );
        items.add( papas );
        items.add( gaseosa );
        Combo combo = new Combo( "combo corral", 0.10, items );
        pedidoCombo = new Pedido( "Carlos", "Calle 3" );
        pedidoCombo.agregarProducto( combo );
 
        // Escenario 4: con producto ajustado (hamburguesa + queso agregado, tomate eliminado)
        ProductoAjustado ajustado = new ProductoAjustado( hamburguesa );
        ajustado.agregarIngrediente( queso );
        ajustado.eliminarIngrediente( tomate );
        pedidoAjustado = new Pedido( "María", "Calle 4" );
        pedidoAjustado.agregarProducto( ajustado );
 
        carpetaTemporal = Files.createTempDirectory( "facturas_test" ).toFile( );
    }
 
    @AfterEach
    void tearDown( ) throws Exception
    {
        pedidoVacio      = null;
        pedidoUnProducto = null;
        pedidoCombo      = null;
        pedidoAjustado   = null;
        for ( File f : carpetaTemporal.listFiles( ) )
            f.delete( );
        carpetaTemporal.delete( );
    }
 
    // ---- getNombreCliente ----
 
    @Test
    void testGetNombreCliente( )
    {
        assertEquals( "Ana", pedidoVacio.getNombreCliente( ),
                "El nombre del cliente no coincide." );
    }
 
    @Test
    void testGetNombreClienteDiferentes( )
    {
        assertEquals( "Luis", pedidoUnProducto.getNombreCliente( ) );
        assertEquals( "Carlos", pedidoCombo.getNombreCliente( ) );
    }
 
    // ---- getIdPedido ----
 
    @Test
    void testGetIdPedidoNoNegativo( )
    {
        assertTrue( pedidoVacio.getIdPedido( ) >= 0,
                "El id del pedido debe ser un número no negativo." );
    }
 
    @Test
    void testDiferentesPedidosTienenIdsDiferentes( )
    {
        assertNotEquals( pedidoVacio.getIdPedido( ), pedidoUnProducto.getIdPedido( ),
                "Cada pedido debe tener un identificador único." );
    }
 
    // ---- getPrecioTotalPedido ----
    // NOTA: getPrecioNetoPedido y getPrecioIVAPedido son privados,
    // solo se puede verificar el total mediante getPrecioTotalPedido.
 
    @Test
    void testPrecioTotalPedidoVacioEsCero( )
    {
        assertEquals( 0, pedidoVacio.getPrecioTotalPedido( ),
                "El precio total de un pedido vacío debe ser 0." );
    }
 
    @Test
    void testPrecioTotalConUnProducto( )
    {
        // neto = 14000, IVA = (int)(14000 * 0.19) = 2660, total = 16660
        assertEquals( 16660, pedidoUnProducto.getPrecioTotalPedido( ),
                "El precio total con una hamburguesa (14000) debe ser 16660." );
    }
 
    @Test
    void testPrecioTotalConCombo( )
    {
        // Precio correcto del combo: (14000+5500+5000) * (1-0.10) = 22050
        // IVA: (int)(22050 * 0.19) = 4189  →  Total: 26239
        // Este test detecta el BUG en Combo.getPrecio() que usa 'descuento' en vez de '1-descuento'
        assertEquals( 26239, pedidoCombo.getPrecioTotalPedido( ),
                "BUG en Combo.getPrecio(): debe calcular precio*(1-descuento), no precio*descuento." );
    }
 
    @Test
    void testPrecioTotalConProductoAjustado( )
    {
        // base 14000 + queso 2500 = 16500
        // IVA: (int)(16500 * 0.19) = 3135  →  Total: 19635
        // Este test detecta el BUG en ProductoAjustado.getPrecio() que retorna 0
        assertEquals( 19635, pedidoAjustado.getPrecioTotalPedido( ),
                "BUG en ProductoAjustado.getPrecio(): debe retornar precioBase + ingredientes agregados." );
    }
 
    // ---- generarTextoFactura ----
 
    @Test
    void testGenerarTextoFacturaTieneNombreCliente( )
    {
        String texto = pedidoUnProducto.generarTextoFactura( );
        assertTrue( texto.contains( "Luis" ),
                "El texto de factura debe contener el nombre del cliente." );
    }
 
    @Test
    void testGenerarTextoFacturaTieneDireccion( )
    {
        String texto = pedidoUnProducto.generarTextoFactura( );
        assertTrue( texto.contains( "Calle 2" ),
                "El texto de factura debe contener la dirección del cliente." );
    }
 
    @Test
    void testGenerarTextoFacturaTieneEtiquetasDePrecios( )
    {
        String texto = pedidoUnProducto.generarTextoFactura( );
        assertTrue( texto.contains( "Precio Neto:" ),  "Debe mostrar 'Precio Neto:'." );
        assertTrue( texto.contains( "IVA:" ),           "Debe mostrar 'IVA:'." );
        assertTrue( texto.contains( "Precio Total:" ),  "Debe mostrar 'Precio Total:'." );
    }
 
    @Test
    void testGenerarTextoFacturaValoresCorrectos( )
    {
        String texto = pedidoUnProducto.generarTextoFactura( );
        assertTrue( texto.contains( "14000" ), "Debe contener el precio neto (14000)." );
        assertTrue( texto.contains( "2660" ),  "Debe contener el IVA (2660)." );
        assertTrue( texto.contains( "16660" ), "Debe contener el precio total (16660)." );
    }
 
    @Test
    void testGenerarTextoFacturaPedidoVacio( )
    {
        String texto = pedidoVacio.generarTextoFactura( );
        assertTrue( texto.contains( "Ana" ), "Debe contener el nombre del cliente." );
        assertTrue( texto.contains( "0" ),   "Debe mostrar precio 0." );
    }
 
    @Test
    void testGenerarTextoFacturaContieneNombreProducto( )
    {
        String texto = pedidoUnProducto.generarTextoFactura( );
        assertTrue( texto.contains( "hamburguesa sencilla" ),
                "La factura debe contener el nombre del producto." );
    }
 
    @Test
    void testGenerarTextoFacturaContieneLineasSeparadoras( )
    {
        String texto = pedidoUnProducto.generarTextoFactura( );
        assertTrue( texto.contains( "----------------" ),
                "La factura debe contener líneas separadoras." );
    }
 
    // ---- guardarFactura ----
 
    @Test
    void testGuardarFacturaCreaArchivo( ) throws FileNotFoundException
    {
        File archivo = new File( carpetaTemporal, "factura_test.txt" );
        pedidoUnProducto.guardarFactura( archivo );
        assertTrue( archivo.exists( ),
                "guardarFactura debe crear el archivo indicado." );
    }
 
    @Test
    void testGuardarFacturaContenidoCorrecto( ) throws IOException
    {
        File archivo = new File( carpetaTemporal, "factura_contenido.txt" );
        pedidoUnProducto.guardarFactura( archivo );
        String contenido = new String( Files.readAllBytes( archivo.toPath( ) ) );
        assertEquals( pedidoUnProducto.generarTextoFactura( ), contenido,
                "El contenido del archivo debe coincidir exactamente con generarTextoFactura()." );
    }
 
    @Test
    void testGuardarFacturaDirectorioInexistenteLanzaExcepcion( )
    {
        File archivoBadPath = new File( "/ruta/que/no/existe/factura.txt" );
        assertThrows( FileNotFoundException.class,
                ( ) -> pedidoUnProducto.guardarFactura( archivoBadPath ),
                "Debe lanzarse FileNotFoundException si el directorio de destino no existe." );
    }
}
