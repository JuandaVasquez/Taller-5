package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.excepciones.HamburguesaException;
import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;


public class RestauranteTest {

    // -------------------------------------------------------
    // TempDir para archivos de prueba y facturas
    // -------------------------------------------------------
    @TempDir
    File tempDir;
 
    // -------------------------------------------------------
    // Atributos
    // -------------------------------------------------------
 
    private Restaurante restauranteVacio;
    private Restaurante restauranteCargado;
 
    // Archivos de datos de prueba creados en tempDir
    private File archivoIngredientes;
    private File archivoMenu;
    private File archivoCombos;
 
    // -------------------------------------------------------
    // Configuración
    // -------------------------------------------------------
 
    @BeforeEach
    void setUp( ) throws Exception
    {
        restauranteVacio  = new Restaurante( );
        restauranteCargado = new Restaurante( );
 
        // ---- ingredientes_test.txt ----
        archivoIngredientes = new File( tempDir, "ingredientes_test.txt" );
        try ( PrintWriter pw = new PrintWriter( archivoIngredientes ) )
        {
            pw.println( "tomate;1000" );
            pw.println( "queso mozzarella;2500" );
            pw.println( "cebolla;800" );
        }
 
        // ---- menu_test.txt ----
        archivoMenu = new File( tempDir, "menu_test.txt" );
        try ( PrintWriter pw = new PrintWriter( archivoMenu ) )
        {
            pw.println( "hamburguesa sencilla;14000" );
            pw.println( "papas medianas;5500" );
            pw.println( "gaseosa;5000" );
        }
 
        // ---- combos_test.txt ----
        // Formato: nombre;descuento%;producto1;producto2
        archivoCombos = new File( tempDir, "combos_test.txt" );
        try ( PrintWriter pw = new PrintWriter( archivoCombos ) )
        {
            pw.println( "combo test;10%;hamburguesa sencilla;papas medianas" );
        }
 
        restauranteCargado.cargarInformacionRestaurante(
                archivoIngredientes, archivoMenu, archivoCombos );
    }
 
    @AfterEach
    void tearDown( )
    {
        restauranteVacio   = null;
        restauranteCargado = null;
    }
 
    // -------------------------------------------------------
    // Estado inicial
    // -------------------------------------------------------
 
    @Test
    void testRestauranteVacioNoPedidos( )
    {
        assertTrue( restauranteVacio.getPedidos( ).isEmpty( ),
                "Un restaurante recién creado no debe tener pedidos." );
    }
 
    @Test
    void testRestauranteVacioNoMenu( )
    {
        assertTrue( restauranteVacio.getMenuBase( ).isEmpty( ),
                "Un restaurante recién creado no debe tener productos en el menú." );
    }
 
    @Test
    void testRestauranteVacioNoCombos( )
    {
        assertTrue( restauranteVacio.getMenuCombos( ).isEmpty( ),
                "Un restaurante recién creado no debe tener combos." );
    }
 
    @Test
    void testRestauranteVacioNoIngredientes( )
    {
        assertTrue( restauranteVacio.getIngredientes( ).isEmpty( ),
                "Un restaurante recién creado no debe tener ingredientes." );
    }
 
    @Test
    void testRestauranteVacioNoPedidoEnCurso( )
    {
        assertNull( restauranteVacio.getPedidoEnCurso( ),
                "Un restaurante recién creado no debe tener un pedido en curso." );
    }
 
    // -------------------------------------------------------
    // cargarInformacionRestaurante — casos válidos
    // -------------------------------------------------------
 
    @Test
    void testCargaIngredientesCorrecta( )
    {
        assertEquals( 3, restauranteCargado.getIngredientes( ).size( ),
                "Deben cargarse exactamente 3 ingredientes." );
    }
 
    @Test
    void testCargaMenuCorrecto( )
    {
        assertEquals( 3, restauranteCargado.getMenuBase( ).size( ),
                "Deben cargarse exactamente 3 productos." );
    }
 
    @Test
    void testCargaCombosCorrecto( )
    {
        assertEquals( 1, restauranteCargado.getMenuCombos( ).size( ),
                "Debe cargarse exactamente 1 combo." );
    }
 
    @Test
    void testNombresIngredientesCargados( )
    {
        String nombres = restauranteCargado.getIngredientes( ).stream( )
                .map( i -> i.getNombre( ) )
                .reduce( "", ( a, b ) -> a + b );
        assertTrue( nombres.contains( "tomate" ),
                "El ingrediente 'tomate' debe estar cargado." );
        assertTrue( nombres.contains( "queso mozzarella" ),
                "El ingrediente 'queso mozzarella' debe estar cargado." );
    }
 
    @Test
    void testNombresProductosCargados( )
    {
        String nombres = restauranteCargado.getMenuBase( ).stream( )
                .map( p -> p.getNombre( ) )
                .reduce( "", ( a, b ) -> a + b );
        assertTrue( nombres.contains( "hamburguesa sencilla" ) );
        assertTrue( nombres.contains( "papas medianas" ) );
    }
 
    @Test
    void testNombreComboCargado( )
    {
        assertEquals( "combo test",
                restauranteCargado.getMenuCombos( ).get( 0 ).getNombre( ),
                "El nombre del combo cargado no es el esperado." );
    }
 
    // -------------------------------------------------------
    // cargarInformacionRestaurante — excepciones
    // -------------------------------------------------------
 
    @Test
    void testCargaIngredientesRepetidosLanzaExcepcion( ) throws IOException
    {
        File rep = new File( tempDir, "ingredientes_repetido.txt" );
        try ( PrintWriter pw = new PrintWriter( rep ) )
        {
            pw.println( "tomate;1000" );
            pw.println( "tomate;2000" );
        }
        Restaurante r = new Restaurante( );
        assertThrows( IngredienteRepetidoException.class,
                ( ) -> r.cargarInformacionRestaurante( rep, archivoMenu, archivoCombos ),
                "Debe lanzarse IngredienteRepetidoException con ingredientes repetidos." );
    }
 
    @Test
    void testCargaMenuRepetidoLanzaExcepcion( ) throws IOException
    {
        File rep = new File( tempDir, "menu_repetido.txt" );
        try ( PrintWriter pw = new PrintWriter( rep ) )
        {
            pw.println( "hamburguesa sencilla;14000" );
            pw.println( "hamburguesa sencilla;16000" );
        }
        Restaurante r = new Restaurante( );
        assertThrows( ProductoRepetidoException.class,
                ( ) -> r.cargarInformacionRestaurante( archivoIngredientes, rep, archivoCombos ),
                "Debe lanzarse ProductoRepetidoException con productos repetidos." );
    }
 
    @Test
    void testCargaCombosRepetidosLanzaExcepcion( ) throws IOException
    {
        File rep = new File( tempDir, "combos_repetido.txt" );
        try ( PrintWriter pw = new PrintWriter( rep ) )
        {
            pw.println( "combo test;10%;hamburguesa sencilla;papas medianas" );
            pw.println( "combo test;5%;hamburguesa sencilla;gaseosa" );
        }
        Restaurante r = new Restaurante( );
        assertThrows( ProductoRepetidoException.class,
                ( ) -> r.cargarInformacionRestaurante( archivoIngredientes, archivoMenu, rep ),
                "Debe lanzarse ProductoRepetidoException con combos repetidos." );
    }
 
    @Test
    void testCargaComboProductoFaltanteLanzaExcepcion( ) throws IOException
    {
        File faltante = new File( tempDir, "combos_faltante.txt" );
        try ( PrintWriter pw = new PrintWriter( faltante ) )
        {
            pw.println( "combo raro;10%;hamburguesa sencilla;PRODUCTO_INEXISTENTE" );
        }
        Restaurante r = new Restaurante( );
        assertThrows( ProductoFaltanteException.class,
                ( ) -> r.cargarInformacionRestaurante( archivoIngredientes, archivoMenu, faltante ),
                "Debe lanzarse ProductoFaltanteException con producto faltante en combo." );
    }
 
    @Test
    void testCargaArchivoInexistenteLanzaExcepcion( )
    {
        Restaurante r = new Restaurante( );
        assertThrows( IOException.class,
                ( ) -> r.cargarInformacionRestaurante(
                        new File( "noexiste.txt" ), archivoMenu, archivoCombos ),
                "Debe lanzarse IOException si el archivo no existe." );
    }
 
    // -------------------------------------------------------
    // iniciarPedido
    // -------------------------------------------------------
 
    @Test
    void testIniciarPedidoCreaElPedido( ) throws YaHayUnPedidoEnCursoException
    {
        restauranteCargado.iniciarPedido( "Juan", "Calle 10" );
        assertNotNull( restauranteCargado.getPedidoEnCurso( ),
                "Después de iniciar un pedido, getPedidoEnCurso no debe ser null." );
    }
 
    @Test
    void testIniciarPedidoGuardaNombreCliente( ) throws YaHayUnPedidoEnCursoException
    {
        restauranteCargado.iniciarPedido( "Juan", "Calle 10" );
        assertEquals( "Juan",
                restauranteCargado.getPedidoEnCurso( ).getNombreCliente( ),
                "El nombre del cliente en el pedido no es el esperado." );
    }
 
    @Test
    void testIniciarPedidoDobleVezLanzaExcepcion( ) throws YaHayUnPedidoEnCursoException
    {
        restauranteCargado.iniciarPedido( "Juan", "Calle 10" );
        assertThrows( YaHayUnPedidoEnCursoException.class,
                ( ) -> restauranteCargado.iniciarPedido( "María", "Calle 11" ),
                "Debe lanzarse YaHayUnPedidoEnCursoException si ya hay un pedido en curso." );
    }
 
    // -------------------------------------------------------
    // cerrarYGuardarPedido
    // -------------------------------------------------------
 
    @Test
    void testCerrarPedidoSinPedidoEnCursoLanzaExcepcion( )
    {
        assertThrows( NoHayPedidoEnCursoException.class,
                ( ) -> restauranteVacio.cerrarYGuardarPedido( ),
                "Debe lanzarse NoHayPedidoEnCursoException si no hay pedido en curso." );
    }
 
    @Test
    void testCerrarPedidoEliminaElPedidoEnCurso( ) throws HamburguesaException, IOException
    {
        // La carpeta ./facturas/ debe existir para que se pueda guardar la factura
        new File( "./facturas" ).mkdirs( );
 
        restauranteCargado.iniciarPedido( "Juan", "Calle 10" );
        restauranteCargado.cerrarYGuardarPedido( );
        assertNull( restauranteCargado.getPedidoEnCurso( ),
                "Después de cerrar el pedido, getPedidoEnCurso debe ser null." );
    }
 
    @Test
    void testCerrarPedidoAgregaAlHistorial( ) throws HamburguesaException, IOException
    {
        // BUG DETECTADO: el método no llama pedidos.add(pedidoEnCurso)
        new File( "./facturas" ).mkdirs( );
 
        restauranteCargado.iniciarPedido( "Juan", "Calle 10" );
        restauranteCargado.cerrarYGuardarPedido( );
        assertEquals( 1, restauranteCargado.getPedidos( ).size( ),
                "BUG: el pedido cerrado debe quedar en el historial de pedidos." );
    }
 
    @Test
    void testCerrarVariosPedidosLosGuardaTodos( ) throws HamburguesaException, IOException
    {
        new File( "./facturas" ).mkdirs( );
 
        restauranteCargado.iniciarPedido( "Cliente1", "Dir1" );
        restauranteCargado.cerrarYGuardarPedido( );
 
        restauranteCargado.iniciarPedido( "Cliente2", "Dir2" );
        restauranteCargado.cerrarYGuardarPedido( );
 
        assertEquals( 2, restauranteCargado.getPedidos( ).size( ),
                "El historial debe contener todos los pedidos cerrados." );
    }
 
    // -------------------------------------------------------
    // Flujo completo de integración
    // -------------------------------------------------------
 
    @Test
    void testFlujoPedidoConProductos( ) throws HamburguesaException, IOException
    {
        new File( "./facturas" ).mkdirs( );
 
        restauranteCargado.iniciarPedido( "Sofía", "Avenida 5" );
        restauranteCargado.getPedidoEnCurso( )
                .agregarProducto( restauranteCargado.getMenuBase( ).get( 0 ) );
        restauranteCargado.getPedidoEnCurso( )
                .agregarProducto( restauranteCargado.getMenuCombos( ).get( 0 ) );
 
        assertTrue( restauranteCargado.getPedidoEnCurso( ).getPrecioTotalPedido( ) > 0,
                "El precio del pedido con productos debe ser mayor a 0." );
 
        restauranteCargado.cerrarYGuardarPedido( );
 
        // BUG: falla hasta que se corrija cerrarYGuardarPedido
        assertEquals( 1, restauranteCargado.getPedidos( ).size( ),
                "El pedido debe quedar registrado en el historial." );
    }
}
