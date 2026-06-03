package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import java.util.ArrayList;
import java.util.Random;

public class Main extends SimpleApplication {

    private Node jugador;
    private Geometry salida;

    private ArrayList<Geometry> paredes = new ArrayList<>();
    private ArrayList<Node> enemigos = new ArrayList<>();
    private ArrayList<Geometry> balas = new ArrayList<>();

    private boolean arriba, abajo, izquierda, derecha;

    private Vector3f direccionDisparo = new Vector3f(0, 0, 1);

    private int vidas = 3;
    private int nivel = 1;
    private final int NIVEL_MAXIMO = 5;

    private boolean juegoTerminado = false;

    private BitmapText textoTablero;
    private BitmapText mensajeFinal;

    private Random random = new Random();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        flyCam.setEnabled(false);

        cam.setLocation(new Vector3f(0, 25, 0));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        crearJugador();
        crearSalida();
        crearLaberinto();
        crearEnemigos();
        configurarTeclas();
        crearTablero();
    }

    private void crearJugador() {
        jugador = crearMonito("Jugador", ColorRGBA.Blue, ColorRGBA.Yellow, 0, 0);
        rootNode.attachChild(jugador);
    }

    private void crearSalida() {

        Box box = new Box(0.6f, 0.1f, 0.6f);
        salida = new Geometry("Salida", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);

        salida.setMaterial(mat);
        salida.setLocalTranslation(8, -0.4f, -8);

        rootNode.attachChild(salida);
    }

    private void crearLaberinto() {

        for (Geometry pared : paredes) {
            rootNode.detachChild(pared);
        }

        paredes.clear();

        for (int i = -10; i <= 10; i++) {
            crearPared(i, 10);
            crearPared(i, -10);
            crearPared(-10, i);
            crearPared(10, i);
        }

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

        if (nivel >= 2) {
            crearPared(-8, 1);
            crearPared(-7, 1);
            crearPared(-6, 1);
            crearPared(5, -6);
        }

        if (nivel >= 3) {
            crearPared(6, 5);
            crearPared(6, 4);
            crearPared(6, 3);
            crearPared(-2, -7);
        }

        if (nivel >= 4) {
            crearPared(-8, -6);
            crearPared(-7, -6);
            crearPared(3, 6);
            crearPared(4, 6);
        }

        if (nivel >= 5) {
            crearPared(-1, 3);
            crearPared(0, 3);
            crearPared(1, 3);
            crearPared(7, -2);
            crearPared(7, -3);
        }
    }

    private void crearEnemigos() {

        for (Node enemigo : enemigos) {
            rootNode.detachChild(enemigo);
        }

        enemigos.clear();

        int cantidadEnemigos = nivel + 1;

        for (int i = 0; i < cantidadEnemigos; i++) {
            crearNuevoEnemigo(random.nextInt(16) - 8, random.nextInt(16) - 8);
        }
    }

    private void crearNuevoEnemigo(float x, float z) {

        Node enemigo = crearMonito("Enemigo", ColorRGBA.Red, ColorRGBA.Orange, x, z);

        enemigos.add(enemigo);
        rootNode.attachChild(enemigo);
    }

    private Node crearMonito(String nombre, ColorRGBA colorCuerpo, ColorRGBA colorCabeza, float x, float z) {

        Node monito = new Node(nombre);

        Box cuerpoBox = new Box(0.3f, 0.5f, 0.2f);
        Geometry cuerpo = new Geometry("Cuerpo", cuerpoBox);

        Material matCuerpo = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCuerpo.setColor("Color", colorCuerpo);

        cuerpo.setMaterial(matCuerpo);
        monito.attachChild(cuerpo);

        Box cabezaBox = new Box(0.2f, 0.2f, 0.2f);
        Geometry cabeza = new Geometry("Cabeza", cabezaBox);

        Material matCabeza = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCabeza.setColor("Color", colorCabeza);

        cabeza.setMaterial(matCabeza);
        cabeza.setLocalTranslation(0, 0.8f, 0);

        monito.attachChild(cabeza);

        monito.setLocalTranslation(x, 0, z);

        return monito;
    }

    private void crearPared(float x, float z) {

        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry pared = new Geometry("Pared", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);

        pared.setMaterial(mat);
        pared.setLocalTranslation(x, 0, z);

        paredes.add(pared);
        rootNode.attachChild(pared);
    }

    private void configurarTeclas() {

        // CONTROLES AL REVÉS
        // W = ABAJO
        inputManager.addMapping("Abajo", new KeyTrigger(KeyInput.KEY_W));

        // S = ARRIBA
        inputManager.addMapping("Arriba", new KeyTrigger(KeyInput.KEY_S));

        // A = DERECHA
        inputManager.addMapping("Derecha", new KeyTrigger(KeyInput.KEY_A));

        // D = IZQUIERDA
        inputManager.addMapping("Izquierda", new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping("Reiniciar", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Disparar", new KeyTrigger(KeyInput.KEY_SPACE));

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

    private ActionListener listener = new ActionListener() {

        @Override
        public void onAction(String name, boolean isPressed, float tpf) {

            if (name.equals("Arriba")) arriba = isPressed;
            if (name.equals("Abajo")) abajo = isPressed;
            if (name.equals("Izquierda")) izquierda = isPressed;
            if (name.equals("Derecha")) derecha = isPressed;

            if (name.equals("Reiniciar") && isPressed) {
                reiniciarJuego();
            }

            if (name.equals("Disparar") && isPressed && !juegoTerminado) {
                disparar();
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {

        if (juegoTerminado) {
            return;
        }

        moverJugador(tpf);
        moverEnemigos(tpf);
        moverBalas(tpf);
        revisarColisiones();
    }

    private void moverJugador(float tpf) {

        Vector3f posicionAnterior = jugador.getLocalTranslation().clone();

        float velocidad = 5f * tpf;

        // S = ARRIBA
        if (arriba) {
            jugador.move(0, 0, -velocidad);
            direccionDisparo = new Vector3f(0, 0, -1);
        }

        // W = ABAJO
        if (abajo) {
            jugador.move(0, 0, velocidad);
            direccionDisparo = new Vector3f(0, 0, 1);
        }

        // D = IZQUIERDA
        if (izquierda) {
            jugador.move(-velocidad, 0, 0);
            direccionDisparo = new Vector3f(-1, 0, 0);
        }

        // A = DERECHA
        if (derecha) {
            jugador.move(velocidad, 0, 0);
            direccionDisparo = new Vector3f(1, 0, 0);
        }

        for (Geometry pared : paredes) {
            if (jugador.getWorldBound().intersects(pared.getWorldBound())) {
                jugador.setLocalTranslation(posicionAnterior);
            }
        }
    }

    private void moverEnemigos(float tpf) {

        float velocidad = (1.5f + nivel * 0.5f) * tpf;

        for (Node enemigo : enemigos) {

            Vector3f direccion = jugador.getLocalTranslation()
                    .subtract(enemigo.getLocalTranslation())
                    .normalizeLocal();

            enemigo.move(direccion.x * velocidad, 0, direccion.z * velocidad);
        }
    }

    private void disparar() {

        Box box = new Box(0.1f, 0.1f, 0.1f);
        Geometry bala = new Geometry("Bala", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);

        bala.setMaterial(mat);
        bala.setLocalTranslation(jugador.getLocalTranslation().clone());

        bala.setUserData("direccion", direccionDisparo.clone());

        balas.add(bala);
        rootNode.attachChild(bala);
    }

    private void moverBalas(float tpf) {

        ArrayList<Geometry> balasEliminar = new ArrayList<>();
        ArrayList<Node> enemigosEliminar = new ArrayList<>();

        for (Geometry bala : balas) {

            Vector3f direccion = bala.getUserData("direccion");

            bala.move(
                    direccion.x * 10f * tpf,
                    0,
                    direccion.z * 10f * tpf
            );

            if (
                    bala.getLocalTranslation().x > 12 ||
                    bala.getLocalTranslation().x < -12 ||
                    bala.getLocalTranslation().z > 12 ||
                    bala.getLocalTranslation().z < -12
            ) {
                balasEliminar.add(bala);
            }

            for (Node enemigo : enemigos) {
                if (bala.getWorldBound().intersects(enemigo.getWorldBound())) {
                    balasEliminar.add(bala);
                    enemigosEliminar.add(enemigo);
                }
            }
        }

        for (Geometry bala : balasEliminar) {
            rootNode.detachChild(bala);
            balas.remove(bala);
        }

        for (Node enemigo : enemigosEliminar) {
            rootNode.detachChild(enemigo);
            enemigos.remove(enemigo);

            crearNuevoEnemigo(random.nextInt(16) - 8, random.nextInt(16) - 8);
        }
    }

    private void revisarColisiones() {

        for (Node enemigo : enemigos) {

            if (jugador.getWorldBound().intersects(enemigo.getWorldBound())) {

                vidas--;
                actualizarTablero();

                jugador.setLocalTranslation(0, 0, 0);

                if (vidas <= 0) {
                    juegoTerminado = true;
                    mostrarMensaje("GAME OVER");
                }
            }
        }

        if (jugador.getWorldBound().intersects(salida.getWorldBound())) {

            if (nivel < NIVEL_MAXIMO) {
                siguienteNivel();
            } else {
                juegoTerminado = true;
                mostrarMensaje("GANASTE EL JUEGO");
            }
        }
    }

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

    private void borrarBalas() {

        for (Geometry bala : balas) {
            rootNode.detachChild(bala);
        }

        balas.clear();
    }

    private void crearTablero() {

        textoTablero = new BitmapText(guiFont, false);
        textoTablero.setSize(30);

        textoTablero.setText("VIDAS: " + vidas + "   NIVEL: " + nivel);

        textoTablero.setLocalTranslation(
                20,
                settings.getHeight() - 20,
                0
        );

        guiNode.attachChild(textoTablero);
    }

    private void actualizarTablero() {

        textoTablero.setText("VIDAS: " + vidas + "   NIVEL: " + nivel);
    }

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

    private void reiniciarJuego() {

        vidas = 3;
        nivel = 1;
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

        if (mensajeFinal != null) {
            guiNode.detachChild(mensajeFinal);
            mensajeFinal = null;
        }
    }
}