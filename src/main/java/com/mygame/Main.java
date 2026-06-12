package com.mygame;

// Importaciones principales de jMonkeyEngine
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

// Importaciones de Java para listas y números aleatorios
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase principal del videojuego.
 * El juego consiste en escapar de un laberinto, evitar enemigos,
 * dispararles y avanzar por varios niveles hasta ganar.
 */
public class Main extends SimpleApplication {

    // Nodo que representa al jugador principal
    private Node jugador;

    // Objeto que representa la salida del laberinto
    private Geometry salida;

    // Lista donde se guardan las paredes del laberinto
    private ArrayList<Geometry> paredes = new ArrayList<>();

    // Lista donde se guardan los enemigos activos
    private ArrayList<Node> enemigos = new ArrayList<>();

    // Lista donde se guardan las balas disparadas por el jugador
    private ArrayList<Geometry> balas = new ArrayList<>();

    // Variables que indican si una tecla de movimiento está presionada
    private boolean arriba, abajo, izquierda, derecha;

    // Dirección en la que dispara el jugador, según su último movimiento
    private Vector3f direccionDisparo = new Vector3f(0, 0, 1);

    // Variables principales del juego
    private int vidas = 3;
    private int nivel = 1;
    private int enemigosMatados = 0;

    // Nivel máximo del juego
    private final int NIVEL_MAXIMO = 5;

    // Variable para saber si el juego terminó
    private boolean juegoTerminado = false;

    // Textos que se muestran en pantalla
    private BitmapText textoTablero;
    private BitmapText mensajeFinal;

    // Objeto para crear posiciones aleatorias de enemigos
    private Random random = new Random();

    /**
     * Método principal. Inicia la aplicación de jMonkeyEngine.
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /**
     * Método inicial de jMonkeyEngine.
     * Aquí se crean todos los elementos iniciales del juego.
     */
    @Override
    public void simpleInitApp() {

        // Se desactiva la cámara libre para que el usuario no la mueva con el mouse
        flyCam.setEnabled(false);

        // Se coloca la cámara en una posición inclinada para ver mejor el laberinto
        cam.setLocation(new Vector3f(0, 18, 18));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        // Se crean los elementos principales del juego
        crearJugador();
        crearSalida();
        crearLaberinto();
        crearEnemigos();
        configurarTeclas();
        crearTablero();
    }

    /**
     * Crea un material de color para aplicarlo a los objetos del juego.
     */
    private Material crearMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
    }

    /**
     * Crea al jugador usando el método crearMonito.
     */
    private void crearJugador() {
        jugador = crearMonito("Jugador", ColorRGBA.Blue, ColorRGBA.Yellow, 0, 0);
        rootNode.attachChild(jugador);
    }

    /**
     * Crea la salida del laberinto de color verde.
     */
    private void crearSalida() {
        salida = new Geometry("Salida", new Box(0.7f, 0.1f, 0.7f));
        salida.setMaterial(crearMaterial(ColorRGBA.Green));
        salida.setLocalTranslation(8, -0.4f, -8);
        rootNode.attachChild(salida);
    }

    /**
     * Crea una pared en una posición específica del mapa.
     */
    private void crearPared(float x, float z) {
        Geometry pared = new Geometry("Pared", new Box(0.5f, 1.5f, 0.5f));
        pared.setMaterial(crearMaterial(ColorRGBA.Gray));
        pared.setLocalTranslation(x, 0.5f, z);

        paredes.add(pared);
        rootNode.attachChild(pared);
    }

    /**
     * Crea el laberinto del juego.
     * Aumenta la dificultad agregando más paredes según el nivel.
     */
    private void crearLaberinto() {

        // Primero se eliminan las paredes anteriores para reconstruir el laberinto
        for (Geometry pared : paredes) {
            rootNode.detachChild(pared);
        }

        paredes.clear();

        // Paredes exteriores que delimitan el escenario
        for (int i = -10; i <= 10; i++) {
            crearPared(i, 10);
            crearPared(i, -10);
            crearPared(-10, i);
            crearPared(10, i);
        }

        // Paredes internas del nivel base
        crearPared(-5, 5);
        crearPared(-4, 5);
        crearPared(-3, 5);

        crearPared(2, 2);
        crearPared(3, 2);
        crearPared(4, 2);

        crearPared(-6, -2);
        crearPared(-5, -2);
        crearPared(-4, -2);

        crearPared(0, -5);
        crearPared(1, -5);
        crearPared(2, -5);

        // A partir del nivel 2 se agregan más obstáculos
        if (nivel >= 2) {
            crearPared(-8, 1);
            crearPared(-7, 1);
            crearPared(-6, 1);
            crearPared(5, -6);
        }

        // A partir del nivel 3 se agregan más paredes
        if (nivel >= 3) {
            crearPared(6, 5);
            crearPared(6, 4);
            crearPared(6, 3);
            crearPared(-2, -7);
        }

        // A partir del nivel 4 aumenta la dificultad del laberinto
        if (nivel >= 4) {
            crearPared(-8, -6);
            crearPared(-7, -6);
            crearPared(3, 6);
            crearPared(4, 6);
        }

        // En el nivel 5 se agregan los últimos obstáculos
        if (nivel >= 5) {
            crearPared(-1, 3);
            crearPared(0, 3);
            crearPared(1, 3);
            crearPared(7, -2);
            crearPared(7, -3);
        }
    }

    /**
     * Crea los enemigos del nivel actual.
     * La cantidad de enemigos aumenta conforme sube el nivel.
     */
    private void crearEnemigos() {

        // Elimina enemigos anteriores
        for (Node enemigo : enemigos) {
            rootNode.detachChild(enemigo);
        }

        enemigos.clear();

        // Se crea una cantidad de enemigos según el nivel actual
        int cantidadEnemigos = nivel + 1;

        for (int i = 0; i < cantidadEnemigos; i++) {
            crearNuevoEnemigo(random.nextInt(16) - 8, random.nextInt(16) - 8);
        }
    }

    /**
     * Crea un enemigo en una posición específica.
     */
    private void crearNuevoEnemigo(float x, float z) {
        Node enemigo = crearMonito("Enemigo", ColorRGBA.Red, ColorRGBA.Orange, x, z);
        enemigos.add(enemigo);
        rootNode.attachChild(enemigo);
    }

    /**
     * Crea un personaje tipo monito con cuerpo, cabeza, ojos, brazos y piernas.
     * Se usa tanto para el jugador como para los enemigos.
     */
    private Node crearMonito(String nombre, ColorRGBA colorCuerpo, ColorRGBA colorCabeza, float x, float z) {

        Node monito = new Node(nombre);

        Material matCuerpo = crearMaterial(colorCuerpo);
        Material matCabeza = crearMaterial(colorCabeza);
        Material matNegro = crearMaterial(ColorRGBA.Black);

        // Cuerpo del personaje
        Geometry cuerpo = new Geometry("Cuerpo", new Box(0.35f, 0.65f, 0.20f));
        cuerpo.setMaterial(matCuerpo);
        cuerpo.setLocalTranslation(0, 0.8f, 0);
        monito.attachChild(cuerpo);

        // Cabeza del personaje
        Geometry cabeza = new Geometry("Cabeza", new Box(0.35f, 0.35f, 0.35f));
        cabeza.setMaterial(matCabeza);
        cabeza.setLocalTranslation(0, 1.65f, 0);
        monito.attachChild(cabeza);

        // Ojo izquierdo
        Geometry ojoIzq = new Geometry("OjoIzq", new Box(0.05f, 0.05f, 0.02f));
        ojoIzq.setMaterial(matNegro);
        ojoIzq.setLocalTranslation(-0.12f, 1.72f, 0.36f);
        monito.attachChild(ojoIzq);

        // Ojo derecho
        Geometry ojoDer = new Geometry("OjoDer", new Box(0.05f, 0.05f, 0.02f));
        ojoDer.setMaterial(matNegro);
        ojoDer.setLocalTranslation(0.12f, 1.72f, 0.36f);
        monito.attachChild(ojoDer);

        // Brazo izquierdo
        Geometry brazoIzq = new Geometry("BrazoIzq", new Box(0.12f, 0.45f, 0.12f));
        brazoIzq.setMaterial(matCuerpo);
        brazoIzq.setLocalTranslation(-0.50f, 0.8f, 0);
        monito.attachChild(brazoIzq);

        // Brazo derecho
        Geometry brazoDer = new Geometry("BrazoDer", new Box(0.12f, 0.45f, 0.12f));
        brazoDer.setMaterial(matCuerpo);
        brazoDer.setLocalTranslation(0.50f, 0.8f, 0);
        monito.attachChild(brazoDer);

        // Pierna izquierda
        Geometry piernaIzq = new Geometry("PiernaIzq", new Box(0.13f, 0.45f, 0.13f));
        piernaIzq.setMaterial(matCuerpo);
        piernaIzq.setLocalTranslation(-0.17f, 0.0f, 0);
        monito.attachChild(piernaIzq);

        // Pierna derecha
        Geometry piernaDer = new Geometry("PiernaDer", new Box(0.13f, 0.45f, 0.13f));
        piernaDer.setMaterial(matCuerpo);
        piernaDer.setLocalTranslation(0.17f, 0.0f, 0);
        monito.attachChild(piernaDer);

        // Posición final del personaje dentro del mundo
        monito.setLocalTranslation(x, 0, z);

        return monito;
    }

    /**
     * Configura los controles del teclado.
     */
    private void configurarTeclas() {

        // Controles de movimiento
        inputManager.addMapping("Arriba", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Abajo", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Izquierda", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Derecha", new KeyTrigger(KeyInput.KEY_D));

        // Controles de acción
        inputManager.addMapping("Reiniciar", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Disparar", new KeyTrigger(KeyInput.KEY_SPACE));

        // Se registra el listener para detectar las teclas
        inputManager.addListener(
                listener,
                "Arriba",
                "Abajo",
                "Izquierda",
                "Derecha",
                "Reiniciar",
                "Disparar"
        );
    }

    /**
     * Listener que detecta cuando se presionan o sueltan teclas.
     */
    private ActionListener listener = new ActionListener() {

        @Override
        public void onAction(String name, boolean isPressed, float tpf) {

            // Se actualizan las variables de movimiento
            if (name.equals("Arriba")) arriba = isPressed;
            if (name.equals("Abajo")) abajo = isPressed;
            if (name.equals("Izquierda")) izquierda = isPressed;
            if (name.equals("Derecha")) derecha = isPressed;

            // Reinicia el juego cuando se presiona R
            if (name.equals("Reiniciar") && isPressed) {
                reiniciarJuego();
            }

            // Dispara cuando se presiona espacio
            if (name.equals("Disparar") && isPressed && !juegoTerminado) {
                disparar();
            }
        }
    };

    /**
     * Método que se ejecuta constantemente durante el juego.
     * Actualiza movimiento, balas, enemigos, animaciones y colisiones.
     */
    @Override
    public void simpleUpdate(float tpf) {

        // Si el juego ya terminó, no se actualiza nada
        if (juegoTerminado) {
            return;
        }

        moverJugador(tpf);
        moverEnemigos(tpf);
        moverBalas(tpf);

        animarMonito(jugador);

        for (Node enemigo : enemigos) {
            animarMonito(enemigo);
        }

        revisarColisiones();
    }

    /**
     * Mueve al jugador según las teclas presionadas.
     * También actualiza la dirección de disparo y evita atravesar paredes.
     */
    private void moverJugador(float tpf) {

        // Guarda la posición anterior para regresar si choca con una pared
        Vector3f posicionAnterior = jugador.getLocalTranslation().clone();

        float velocidad = 5f * tpf;

        // W = arriba
        if (arriba) {
            jugador.move(0, 0, -velocidad);
            direccionDisparo = new Vector3f(0, 0, -1);
            jugador.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y));
        }

        // S = abajo
        if (abajo) {
            jugador.move(0, 0, velocidad);
            direccionDisparo = new Vector3f(0, 0, 1);
            jugador.setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));
        }

        // A = izquierda
        if (izquierda) {
            jugador.move(-velocidad, 0, 0);
            direccionDisparo = new Vector3f(-1, 0, 0);
            jugador.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
        }

        // D = derecha
        if (derecha) {
            jugador.move(velocidad, 0, 0);
            direccionDisparo = new Vector3f(1, 0, 0);
            jugador.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
        }

        // Si el jugador choca contra una pared, vuelve a la posición anterior
        for (Geometry pared : paredes) {
            if (jugador.getWorldBound().intersects(pared.getWorldBound())) {
                jugador.setLocalTranslation(posicionAnterior);
            }
        }
    }

    /**
     * Mueve a los enemigos automáticamente hacia la posición del jugador.
     */
    private void moverEnemigos(float tpf) {

        // La velocidad aumenta con el nivel
        float velocidad = (1.5f + nivel * 0.5f) * tpf;

        for (Node enemigo : enemigos) {

            // Calcula dirección del enemigo hacia el jugador
            Vector3f direccion = jugador.getLocalTranslation()
                    .subtract(enemigo.getLocalTranslation());

            if (direccion.length() > 0) {

                direccion.normalizeLocal();

                // Mueve al enemigo en dirección al jugador
                enemigo.move(direccion.x * velocidad, 0, direccion.z * velocidad);

                // Gira al enemigo hacia donde se mueve
                float angulo = FastMath.atan2(direccion.x, direccion.z);
                enemigo.setLocalRotation(new Quaternion().fromAngleAxis(angulo, Vector3f.UNIT_Y));
            }
        }
    }

    /**
     * Anima brazos y piernas del monito para simular que camina.
     */
    private void animarMonito(Node monito) {

        float tiempo = timer.getTimeInSeconds();
        float movimiento = FastMath.sin(tiempo * 8f) * 0.25f;

        Geometry brazoIzq = (Geometry) monito.getChild("BrazoIzq");
        Geometry brazoDer = (Geometry) monito.getChild("BrazoDer");
        Geometry piernaIzq = (Geometry) monito.getChild("PiernaIzq");
        Geometry piernaDer = (Geometry) monito.getChild("PiernaDer");

        // Se mueven brazos y piernas en direcciones contrarias
        if (brazoIzq != null) {
            brazoIzq.setLocalTranslation(-0.50f, 0.8f, movimiento);
        }

        if (brazoDer != null) {
            brazoDer.setLocalTranslation(0.50f, 0.8f, -movimiento);
        }

        if (piernaIzq != null) {
            piernaIzq.setLocalTranslation(-0.17f, 0.0f, -movimiento);
        }

        if (piernaDer != null) {
            piernaDer.setLocalTranslation(0.17f, 0.0f, movimiento);
        }
    }

    /**
     * Crea una bala en la posición del jugador.
     */
    private void disparar() {

        Geometry bala = new Geometry("Bala", new Box(0.12f, 0.12f, 0.12f));

        bala.setMaterial(crearMaterial(ColorRGBA.Yellow));

        Vector3f posicionBala = jugador.getLocalTranslation().clone();
        posicionBala.y = 1.1f;

        bala.setLocalTranslation(posicionBala);

        // Se guarda la dirección en la que viajará la bala
        bala.setUserData("direccion", direccionDisparo.clone());

        balas.add(bala);
        rootNode.attachChild(bala);
    }

    /**
     * Mueve las balas, elimina las que salen del mapa
     * y revisa si golpean a algún enemigo.
     */
    private void moverBalas(float tpf) {

        ArrayList<Geometry> balasEliminar = new ArrayList<>();
        ArrayList<Node> enemigosEliminar = new ArrayList<>();

        for (Geometry bala : balas) {

            Vector3f direccion = bala.getUserData("direccion");

            // Movimiento de la bala
            bala.move(
                    direccion.x * 10f * tpf,
                    0,
                    direccion.z * 10f * tpf
            );

            // Si la bala sale del escenario, se marca para eliminar
            if (
                    bala.getLocalTranslation().x > 12 ||
                    bala.getLocalTranslation().x < -12 ||
                    bala.getLocalTranslation().z > 12 ||
                    bala.getLocalTranslation().z < -12
            ) {
                balasEliminar.add(bala);
            }

            // Si la bala toca un enemigo, ambos se eliminan
            for (Node enemigo : enemigos) {
                if (bala.getWorldBound().intersects(enemigo.getWorldBound())) {
                    balasEliminar.add(bala);
                    enemigosEliminar.add(enemigo);
                }
            }
        }

        // Elimina balas marcadas
        for (Geometry bala : balasEliminar) {
            rootNode.detachChild(bala);
            balas.remove(bala);
        }

        // Elimina enemigos, aumenta contador y genera uno nuevo
        for (Node enemigo : enemigosEliminar) {
            rootNode.detachChild(enemigo);
            enemigos.remove(enemigo);

            enemigosMatados++;
            actualizarTablero();

            crearNuevoEnemigo(random.nextInt(16) - 8, random.nextInt(16) - 8);
        }
    }

    /**
     * Revisa colisiones entre jugador, enemigos y salida.
     */
    private void revisarColisiones() {

        // Colisión entre jugador y enemigos
        for (Node enemigo : enemigos) {

            if (jugador.getWorldBound().intersects(enemigo.getWorldBound())) {

                vidas--;
                actualizarTablero();

                // Se regresa al jugador al centro
                jugador.setLocalTranslation(0, 0, 0);

                // Si las vidas llegan a cero, termina el juego
                if (vidas <= 0) {
                    juegoTerminado = true;
                    mostrarMensaje("GAME OVER");
                }
            }
        }

        // Colisión entre jugador y salida
        if (jugador.getWorldBound().intersects(salida.getWorldBound())) {

            if (nivel < NIVEL_MAXIMO) {
                siguienteNivel();
            } else {
                juegoTerminado = true;
                mostrarMensaje("GANASTE EL JUEGO");
            }
        }
    }

    /**
     * Avanza al siguiente nivel, reinicia vidas y reconstruye el escenario.
     */
    private void siguienteNivel() {

        nivel++;
        vidas = 3;

        jugador.setLocalTranslation(0, 0, 0);

        borrarBalas();
        crearLaberinto();
        crearEnemigos();
        actualizarTablero();

        mostrarMensaje("NIVEL " + nivel);
    }

    /**
     * Elimina todas las balas activas del juego.
     */
    private void borrarBalas() {

        for (Geometry bala : balas) {
            rootNode.detachChild(bala);
        }

        balas.clear();
    }

    /**
     * Crea el tablero de información del juego.
     */
    private void crearTablero() {

        textoTablero = new BitmapText(guiFont, false);
        textoTablero.setSize(28);

        textoTablero.setText(
                "VIDAS: " + vidas +
                        "   NIVEL: " + nivel +
                        "   ENEMIGOS MATADOS: " + enemigosMatados
        );

        textoTablero.setLocalTranslation(20, settings.getHeight() - 20, 0);

        guiNode.attachChild(textoTablero);
    }

    /**
     * Actualiza los datos del tablero.
     */
    private void actualizarTablero() {

        textoTablero.setText(
                "VIDAS: " + vidas +
                        "   NIVEL: " + nivel +
                        "   ENEMIGOS MATADOS: " + enemigosMatados
        );
    }

    /**
     * Muestra un mensaje grande en pantalla.
     */
    private void mostrarMensaje(String mensaje) {

        if (mensajeFinal != null) {
            guiNode.detachChild(mensajeFinal);
        }

        mensajeFinal = new BitmapText(guiFont, false);
        mensajeFinal.setSize(60);
        mensajeFinal.setText(mensaje);

        mensajeFinal.setLocalTranslation(
                settings.getWidth() / 2f - 250,
                settings.getHeight() / 2f,
                0
        );

        guiNode.attachChild(mensajeFinal);
    }

    /**
     * Reinicia el juego desde el nivel 1.
     */
    private void reiniciarJuego() {

        vidas = 3;
        nivel = 1;
        enemigosMatados = 0;
        juegoTerminado = false;

        arriba = false;
        abajo = false;
        izquierda = false;
        derecha = false;

        jugador.setLocalTranslation(0, 0, 0);

        borrarBalas();
        crearLaberinto();
        crearEnemigos();
        actualizarTablero();

        // Elimina mensajes de GAME OVER o victoria si existían
        if (mensajeFinal != null) {
            guiNode.detachChild(mensajeFinal);
            mensajeFinal = null;
        }
    }
}
