package com.example.josu.bathunting;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class Vista extends SurfaceView implements SurfaceHolder.Callback {

    private Context contexto;
    private SurfaceHolder surfaceHolder;
    private Bitmap fondo;
    private int anchoFondo;
    private int altoFondo;
    private float escalaAncho;
    private float escalaAlto;
    private float escalaDibujoAncho;
    private float escalaDibujoAlto;
    private Bitmap mascara;
    private Bitmap murcielago;
    private int murcielago1x, murcielago2x, murcielago3x, murcielago4x, murcielago5x, murcielago6x, murcielago7x;
    private int murcielago1y, murcielago2y, murcielago3y, murcielago4y, murcielago5y, murcielago6y, murcielago7y;
    private int anchoPantalla = 1;
    private int altoPantalla = 1;
    private boolean running = false;
    private boolean portada = true;
    private int murcielagoActual = 0;
    private boolean apareciendo = true;
    private boolean desapareciendo = false;
    private int velocidadMurcielago = 5;
    private int posicionX, posicionY;
    private static SoundPool sonidos;
    private static int sonidoGolpe;
    private static int sonidoBuh;
    private boolean golpeado = false;
    private Bitmap golpe;
    private boolean golpeando = false;
    private int cazados = 0;
    private int libres = 0;
    private Paint pincel;
    private boolean gameOver = false;
    private Bitmap gameOverVentana;
    private int puntuacionMaxima;
    private Hilo hilo;

    public Vista(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        hilo = new Hilo(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });
        setFocusable(true);
    }

    public Hilo getHilo() {
        return hilo;
    }

    class Hilo extends Thread {

        public Hilo(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            Vista.this.surfaceHolder = surfaceHolder;
            contexto = context;
            fondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.portada);
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
            sonidos = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            sonidoGolpe = sonidos.load(contexto, R.raw.golpe, 1);
            sonidoBuh = sonidos.load(contexto, R.raw.buh, 1);
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        if (!gameOver) {
                            moverMurcielago();
                        }
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            try {
                canvas.drawBitmap(fondo, 0, 0, null);
                if(portada){
                    puntuacionMaxima = Integer.valueOf(getPreferenciasCompartidas());
                    canvas.drawText(getResources().getString(R.string.puntuacion_maxima) + " " + puntuacionMaxima, 20, altoPantalla-40, pincel);
                }else {
                    canvas.drawBitmap(murcielago, murcielago1x, murcielago1y, null);
                    canvas.drawBitmap(murcielago, murcielago2x, murcielago2y, null);
                    canvas.drawBitmap(murcielago, murcielago3x, murcielago3y, null);
                    canvas.drawBitmap(murcielago, murcielago4x, murcielago4y, null);
                    canvas.drawBitmap(murcielago, murcielago5x, murcielago5y, null);
                    canvas.drawBitmap(murcielago, murcielago6x, murcielago6y, null);
                    canvas.drawBitmap(murcielago, murcielago7x, murcielago7y, null);
                    canvas.drawBitmap(mascara, (int) 50 * escalaDibujoAncho, (int) -75 * escalaDibujoAlto, null);
                    canvas.drawBitmap(mascara, (int) 150 * escalaDibujoAncho, (int) -25 * escalaDibujoAlto, null);
                    canvas.drawBitmap(mascara, (int) 250 * escalaDibujoAncho, (int) -75 * escalaDibujoAlto, null);
                    canvas.drawBitmap(mascara, (int) 350 * escalaDibujoAncho, (int) -25 * escalaDibujoAlto, null);
                    canvas.drawBitmap(mascara, (int) 450 * escalaDibujoAncho, (int) -75 * escalaDibujoAlto, null);
                    canvas.drawBitmap(mascara, (int) 550 * escalaDibujoAncho, (int) -25 * escalaDibujoAlto, null);
                    canvas.drawBitmap(mascara, (int) 650 * escalaDibujoAncho, (int) -75 * escalaDibujoAlto, null);
                    canvas.drawText(getResources().getString(R.string.cazados) + " " + Integer.toString(cazados), 10, pincel.getTextSize() + 10, pincel);
                    canvas.drawText(getResources().getString(R.string.libres) + " " + Integer.toString(libres), anchoPantalla - (int) (200 * escalaDibujoAncho), pincel.getTextSize() + 10, pincel);
                }
                if (golpeando) {
                    canvas.drawBitmap(golpe, posicionX - (golpe.getWidth() / 2), posicionY - (golpe.getHeight() / 2), null);
                }
                if (gameOver) {
                    canvas.drawBitmap(gameOverVentana, (anchoPantalla / 2) - (gameOverVentana.getWidth() / 2), (altoPantalla / 2) - (gameOverVentana.getHeight() / 2), null);
                    if(cazados > puntuacionMaxima)
                        setPreferenciasCompartidas(String.valueOf(cazados));
                }
            } catch (Exception e) {
            }
        }

        boolean doTouchEvent(MotionEvent event) {
            synchronized (surfaceHolder) {
                int eventaction = event.getAction();
                int X = (int) event.getX();
                int Y = (int) event.getY();

                switch (eventaction) {

                    case MotionEvent.ACTION_DOWN:
                        if (!gameOver) {
                            posicionX = X;
                            posicionY = Y;
                            if (!portada && colision()) {
                                golpeando = true;
                                AudioManager audioManager = (AudioManager) contexto.getSystemService(Context.AUDIO_SERVICE);
                                float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                sonidos.play(sonidoGolpe, volume, volume, 1, 0, 1);
                                cazados++;
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        if (portada) {
                            fondo = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.fondo);
                            fondo = Bitmap.createScaledBitmap(fondo, anchoPantalla, altoPantalla, true);
                            mascara = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.mascara);
                            murcielago = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.murcielago);
                            golpe = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.golpe);
                            gameOverVentana = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.gameover);
                            escalaAncho = (float) anchoPantalla / (float) anchoFondo;
                            escalaAlto = (float) altoPantalla / (float) altoFondo;
                            mascara = Bitmap.createScaledBitmap(mascara, (int) (mascara.getWidth() * escalaAncho), (int) (mascara.getHeight() * escalaAlto), true);
                            murcielago = Bitmap.createScaledBitmap(murcielago, (int) (murcielago.getWidth() * escalaAncho), (int) (murcielago.getHeight() * escalaAlto), true);
                            golpe = Bitmap.createScaledBitmap(golpe, (int) (golpe.getWidth() * escalaAncho), (int) (golpe.getHeight() * escalaAlto), true);
                            gameOverVentana = Bitmap.createScaledBitmap(gameOverVentana, (int) (gameOverVentana.getWidth() * escalaAncho), (int) (gameOverVentana.getHeight() * escalaAlto), true);
                            portada = false;
                            elegirMurcielago();
                        }
                        golpeando = false;
                        if (gameOver) {
                            cazados = 0;
                            libres = 0;
                            murcielagoActual = 0;
                            elegirMurcielago();
                            gameOver = false;
                        }
                        break;
                }
            }
            return true;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (surfaceHolder) {
                anchoPantalla = width;
                altoPantalla = height;
                fondo = Bitmap.createScaledBitmap(fondo, width, height, true);
                escalaDibujoAncho = (float) anchoPantalla / 800;
                escalaDibujoAlto = (float) altoPantalla / 600;
                murcielago1x = (int) (55 * escalaDibujoAncho);
                murcielago2x = (int) (155 * escalaDibujoAncho);
                murcielago3x = (int) (255 * escalaDibujoAncho);
                murcielago4x = (int) (355 * escalaDibujoAncho);
                murcielago5x = (int) (455 * escalaDibujoAncho);
                murcielago6x = (int) (555 * escalaDibujoAncho);
                murcielago7x = (int) (655 * escalaDibujoAncho);
                murcielago1y = (int) (-75 * escalaDibujoAlto);
                murcielago2y = (int) (-25 * escalaDibujoAlto);
                murcielago3y = (int) (-75 * escalaDibujoAlto);
                murcielago4y = (int) (-25 * escalaDibujoAlto);
                murcielago5y = (int) (-75 * escalaDibujoAlto);
                murcielago6y = (int) (-25 * escalaDibujoAlto);
                murcielago7y = (int) (-75 * escalaDibujoAlto);
                pincel = new Paint();
                pincel.setAntiAlias(true);
                pincel.setColor(Color.BLACK);
                pincel.setStyle(Paint.Style.STROKE);
                pincel.setTextAlign(Paint.Align.LEFT);
                pincel.setTextSize(escalaDibujoAncho * 30);
            }
        }

        public void setRunning(boolean b) {
            running = b;
        }

        private void moverMurcielago() {
            if (murcielagoActual == 1) {
                if (apareciendo) {
                    murcielago1y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago1y -= velocidadMurcielago;
                }
                if (murcielago1y <= (int) (-75 * escalaDibujoAlto) || golpeado) {
                    murcielago1y = (int) (-75 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago1y >= (int) (100 * escalaDibujoAlto)) {
                    murcielago1y = (int) (100 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
            if (murcielagoActual == 2) {
                if (apareciendo) {
                    murcielago2y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago2y -= velocidadMurcielago;
                }
                if (murcielago2y <= (int) (-25 * escalaDibujoAlto) || golpeado) {
                    murcielago2y = (int) (-25 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago2y >= (int) (150 * escalaDibujoAlto)) {
                    murcielago2y = (int) (150 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
            if (murcielagoActual == 3) {
                if (apareciendo) {
                    murcielago3y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago3y -= velocidadMurcielago;
                }
                if (murcielago3y <= (int) (-75 * escalaDibujoAlto) || golpeado) {
                    murcielago3y = (int) (-75 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago3y >= (int) (100 * escalaDibujoAlto)) {
                    murcielago3y = (int) (100 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
            if (murcielagoActual == 4) {
                if (apareciendo) {
                    murcielago4y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago4y -= velocidadMurcielago;
                }
                if (murcielago4y <= (int) (-25 * escalaDibujoAlto) || golpeado) {
                    murcielago4y = (int) (-25 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago4y >= (int) (150 * escalaDibujoAlto)) {
                    murcielago4y = (int) (150 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
            if (murcielagoActual == 5) {
                if (apareciendo) {
                    murcielago5y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago5y -= velocidadMurcielago;
                }
                if (murcielago5y <= (int) (-75 * escalaDibujoAlto) || golpeado) {
                    murcielago5y = (int) (-75 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago5y >= (int) (100 * escalaDibujoAlto)) {
                    murcielago5y = (int) (100 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
            if (murcielagoActual == 6) {
                if (apareciendo) {
                    murcielago6y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago6y -= velocidadMurcielago;
                }
                if (murcielago6y <= (int) (-25 * escalaDibujoAlto) || golpeado) {
                    murcielago6y = (int) (-25 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago6y >= (int) (150 * escalaDibujoAlto)) {
                    murcielago6y = (int) (150 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
            if (murcielagoActual == 7) {
                if (apareciendo) {
                    murcielago7y += velocidadMurcielago;
                } else if (desapareciendo) {
                    murcielago7y -= velocidadMurcielago;
                }
                if (murcielago7y <= (int) (-75 * escalaDibujoAlto) || golpeado) {
                    murcielago7y = (int) (-75 * escalaDibujoAlto);
                    elegirMurcielago();
                }
                if (murcielago7y >= (int) (100 * escalaDibujoAlto)) {
                    murcielago7y = (int) (100 * escalaDibujoAlto);
                    apareciendo = false;
                    desapareciendo = true;
                }
            }
        }

        private void elegirMurcielago() {
            if (!golpeado && murcielagoActual > 0) {
                AudioManager audioManager = (AudioManager) contexto.getSystemService(Context.AUDIO_SERVICE);
                float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                sonidos.play(sonidoBuh, volume, volume, 1, 0, 1);
                libres++;
                if (libres > 4) {
                    gameOver = true;
                }
            }
            murcielagoActual = new Random().nextInt(7) + 1;
            apareciendo = true;
            desapareciendo = false;
            golpeado = false;
            velocidadMurcielago = 5 + (int) (cazados / 10);
        }

        private boolean colision() {
            boolean contact = false;
            if (murcielagoActual == 1 &&
                    posicionX >= murcielago1x &&
                    posicionX < murcielago1x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago1y &&
                    posicionY < (int) 350 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            if (murcielagoActual == 2 &&
                    posicionX >= murcielago2x &&
                    posicionX < murcielago2x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago2y &&
                    posicionY < (int) 300 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            if (murcielagoActual == 3 &&
                    posicionX >= murcielago3x &&
                    posicionX < murcielago3x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago3y &&
                    posicionY < (int) 350 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            if (murcielagoActual == 4 &&
                    posicionX >= murcielago4x &&
                    posicionX < murcielago4x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago4y &&
                    posicionY < (int) 300 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            if (murcielagoActual == 5 &&
                    posicionX >= murcielago5x &&
                    posicionX < murcielago5x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago5y &&
                    posicionY < (int) 350 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            if (murcielagoActual == 6 &&
                    posicionX >= murcielago6x &&
                    posicionX < murcielago6x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago6y &&
                    posicionY < (int) 300 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            if (murcielagoActual == 7 &&
                    posicionX >= murcielago7x &&
                    posicionX < murcielago7x + (int) (88 * escalaDibujoAncho) &&
                    posicionY > murcielago7y &&
                    posicionY < (int) 350 * escalaDibujoAlto) {
                contact = true;
                golpeado = true;
            }
            return contact;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return hilo.doTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        hilo.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        hilo.setRunning(true);
        if (hilo.getState() == Thread.State.NEW) {
            hilo.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hilo.setRunning(false);
    }

    public void tostada(String mensaje) {
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
    }

    public String getPreferenciasCompartidas(){
        SharedPreferences pc;
        pc = contexto.getSharedPreferences("puntuacionMaxima", Context.MODE_PRIVATE);
        String r = pc.getString("puntuacion", "0");
        return r;
    }

    public void setPreferenciasCompartidas(String puntuacion){
        SharedPreferences pc;
        pc = contexto.getSharedPreferences("puntuacionMaxima", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pc.edit();
        ed.putString("puntuacion", puntuacion);
        ed.commit();
    }


}
