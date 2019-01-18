package com.nave.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Nave implements Screen {
   final Drop game; 
    
   private Texture imagenMisil1; 
   private Texture imagenMisil2;
   private Texture imagenNave1;
   private Texture imagenNave2;
   private Texture imagenExplosion;
   private Sound sonidoExplosion;
   private Sound sonidoDisparoLaser;
   private Music musicaJuego;
   private SpriteBatch batch;
   private OrthographicCamera camara;
   private Rectangle nave;
   private Rectangle nave2;
   private Array<Rectangle> misiles;
   private Array<Rectangle> misiles2;
   private long tiempoCaidaUltimoMisil;
   private long ultimoDisparoMisil;
   private int puntos;
   private int vidas;
   private int vidasNave2;
   private int velocidad = 100;
   
   public Nave(final Drop game) {
       this.game = game;
       
       this.puntos = 0;
       this.vidas = 3;
       this.vidasNave2 = 5;
       
       // carga las imágenes de las gotas de lluvia y del nave, cada una de 64x64 píxeles
      imagenMisil1 = new Texture(Gdx.files.internal("spaceMissiles_001.png"));
      imagenMisil2 = new Texture(Gdx.files.internal("spaceMissiles_002.png"));
      imagenNave1 = new Texture(Gdx.files.internal("spaceShips_008.png"));
      imagenNave2 = new Texture(Gdx.files.internal("spaceShips_001.png"));
      imagenExplosion = new Texture(Gdx.files.internal("tank_explosion4.png"));
      
      // carga de sonido para la caída de la gota y la música de fondo
      sonidoExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.mp3"));
      sonidoDisparoLaser = Gdx.audio.newSound(Gdx.files.internal("disparoLaser.mp3"));
      musicaJuego = Gdx.audio.newMusic(Gdx.files.internal("gta-san-andreas-f.mp3"));

      // se aplica que la música se repita en bucle, comienza la reproducción de la música de fondo
      musicaJuego.setLooping(true);
      musicaJuego.play();
      
      // crea la cámara ortográfica y el lote de sprites
      camara = new OrthographicCamera();
      camara.setToOrtho(false, 800, 480);
      batch = new SpriteBatch();
      
      // crea un rectángulo (clase Rectangle) para representar lógicamente la nave 1
      nave = new Rectangle();
      nave.x = 800 / 2 - 64 / 2; // centra el nave horizontal
      nave.y = 20; // esquina inferior izquierda del nave estará a 20 píxeles del límite inferior
      nave.width = 64;
      nave.height = 64;
      
      // crea un rectangulo (clase Rectangle) para representar logicamente la nave 2
      nave2 = new Rectangle();
      nave2.x = 800 / 2 - 64 / 2; // centra el nave horizontal
      nave2.y = 400;
      nave2.width = 64;
      nave2.height = 64;

      // crea el vector de misiles
      misiles = new Array<Rectangle>();
      misiles2 = new Array<Rectangle>();
      
   }

   private void creaMisil() {
      Rectangle misil = new Rectangle();
      misil.x = nave.x + 38;
      misil.y = nave.y;
      misil.width = 20;
      misil.height = 64;
      misiles.add(misil);
      ultimoDisparoMisil = TimeUtils.nanoTime();
   }
   
   private void creaMisil2() {
       Rectangle misil2 = new Rectangle();
       misil2.x = nave2.x + 38;
       misil2.y = nave2.y;
       misil2.width = 20;
       misil2.height = 64;
       misiles2.add(misil2);
       tiempoCaidaUltimoMisil = TimeUtils.nanoTime();
   }

   public void automaticMove() {
       nave2.x += velocidad * Gdx.graphics.getDeltaTime();
      
      if (nave2.x > 800 - 95 || nave2.x < 0) {
          velocidad = -velocidad;
      }
   }
   
   public void crearNave2() {
        // crea un rectangulo (clase Rectangle) para representar logicamente la nave 2
      nave2 = new Rectangle();
      nave2.x = 800 / 2 - 64 / 2; // centra el nave horizontal
      nave2.y = 400;
      nave2.width = 64;
      nave2.height = 64;
   }
   
   @Override
   public void render(float delta) {
      // limpia la pantalla con un color azul oscuro. Los argumentos RGB de la función glClearcColor están en el rango entre 0 y 1
      Gdx.gl.glClearColor(0, 0, 0.2f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      // ordenada a la cámara actualizar sus matrices
      camara.update();

      // indica al lote de sprites que se represente en las coordenadas específicas de la cámara
      game.batch.setProjectionMatrix(camara.combined);

      // comienza un nuevo proceso y dibuja el nave y los misiles
      game.batch.begin();
      game.batch.draw(imagenNave1, nave.x, nave.y);
      game.batch.draw(imagenNave2, nave2.x, nave2.y);
      game.font.draw(game.batch, "Vidas:  " + vidas, 100, 100);
      game.font.draw(game.batch, "Puntos:  " + puntos, 100, 130);
      
      for(Rectangle misil: misiles) {
         game.batch.draw(imagenMisil1, misil.x, misil.y);
      }
      
      for(Rectangle misil2: misiles2) {
          game.batch.draw(imagenMisil2, misil2.x, misil2.y);
      }
      
      game.batch.end();

      // lectura de entrada
      if(Gdx.input.isTouched()) {
         Vector3 posicionTocada = new Vector3();
         posicionTocada.set(Gdx.input.getX(), Gdx.input.getY(), 0);
         camara.unproject(posicionTocada);
         nave.x = posicionTocada.x - 64 / 2;
      }
      
      if(Gdx.input.isKeyPressed(Keys.LEFT)) nave.x -= 600 * Gdx.graphics.getDeltaTime();
      if(Gdx.input.isKeyPressed(Keys.RIGHT)) nave.x += 600 * Gdx.graphics.getDeltaTime();
      
      //Controla el intervalo entre disparo y disparo de misiles
      if (TimeUtils.nanoTime() - ultimoDisparoMisil > 1000000000) {
        if(Gdx.input.isKeyPressed(Keys.UP)) { 
            creaMisil();
            sonidoDisparoLaser.play();
        }
      }  
      
      // comprueba si ha pasado un segundo desde el último misil2, para crear uno nuevo
      if(TimeUtils.nanoTime() - tiempoCaidaUltimoMisil > 2000000000) creaMisil2();
      
      //Movimiento automatico nave 2
      automaticMove();

      // nos aseguramos de que las naves permanezca entre los límites de la pantalla
      if(nave.x < 0) nave.x = 0;
      if(nave.x > 800 - 96) nave.x = 800 - 96;
      
      if(nave2.x < 0) nave2.x = 0;
      if(nave2.x > 800 - 96) nave2.x = 800 - 96;

      // recorre los misiles y borra aquellos que hayan llegado al suelo (límite superior de la pantalla) o toquen el nave, en ese caso se reproduce sonido.
      Iterator<Rectangle> iter = misiles.iterator();
      while(iter.hasNext()) {
         Rectangle misil = iter.next();
         misil.y += 200 * Gdx.graphics.getDeltaTime();
         if (misil.y + 64 > 800) iter.remove();
         if (misil.overlaps(nave2)) {
            iter.remove();
            puntos++;   
            vidasNave2--;
         }
      }
      
      Iterator<Rectangle> iter2 = misiles2.iterator();
      while(iter2.hasNext()) {
          Rectangle misil2 = iter2.next();
          misil2.y -= 150 * Gdx.graphics.getDeltaTime();
          if (misil2.y + 64 < 0) iter2.remove();
          if (misil2.overlaps(nave)) {
              iter2.remove();
              vidas--;
          }
      }
      
      //Si alcanza cero vidas llama a la pantalla LoseScreen
      if (vidas == 0) {
          
          game.batch.begin();
          
          sonidoExplosion.play();
          game.batch.draw(imagenExplosion, nave.x, nave.y);
          
          game.batch.end();
          
          if (TimeUtils.nanoTime() - ultimoDisparoMisil > 2000000000) {
              game.setScreen(new LoseScreen(game, puntos));
              dispose();
          }
          
      }
      
      //Si eliminas la nave enemiga
      if (vidasNave2 == 0) {
          game.batch.begin();
          
          sonidoExplosion.play();
          game.batch.draw(imagenExplosion, nave2.x, nave2.y);
          
          if (TimeUtils.nanoTime() - ultimoDisparoMisil > 2000000000) {
              velocidad = velocidad + 100;
              vidasNave2 = 5;
              nave2.x = 64;
          }
          
          game.batch.end();
         
      }
      
   }

   @Override
   public void dispose() {
      // liberamos todos los recursos
      imagenMisil1.dispose();
      imagenNave1.dispose();
      //sonicoCaidaGota.dispose();
      //musicaLluvia.dispose();
      batch.dispose();
   }

    @Override
    public void show() {
        
    }

    @Override
    public void resize(int width, int height) {
        
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void hide() {
        
    }
}
