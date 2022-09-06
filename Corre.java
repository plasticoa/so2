import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
public class Corre {
    private static int minLlegada=3,
            maxLlegada=10,
            minAtencion=4,
            maxAtencion=20;
    public static void main(String[] args) throws InterruptedException {

    int cantidadBarberos= 3, idCliente=1, cantidadClientes=4, cantidadSillas=10;
        //generar hilos de los barberos
        for(int i=1; i<=cantidadBarberos; i++) {
            correBarbero obj = new correBarbero(i);
            Thread thread = new Thread(obj);
            thread.start();
        }
        for(int i=1; i<=cantidadClientes; i++){
            correClientes obj = new correClientes();
            obj.setIdCliente(idCliente++);
            Thread hiloCliente = new Thread(obj);
            hiloCliente.start();
            //calcular tiempo de llegada
            int tiempoLlegada = ((int)Math.floor(Math.random()*(maxLlegada-minLlegada+1)+minLlegada)*(1000));

            System.out.println("\n Siguiente cliente llega en (segundos): "+tiempoLlegada/1000);
            Thread.sleep(tiempoLlegada); //dormir el hilo el tiempo random hasta que llega el otro cliente

        }


        }
}
class correBarbero implements  Runnable{
    int idBarbero;
    public correBarbero(int idBarbero){
        this.idBarbero = idBarbero;
    }
    public void run() {
        System.out.println("Barbero "+idBarbero+" listo para trabajar id hilo "
                +Thread.currentThread().getId());
    }
}

class correClientes implements Runnable{
    int idCliente;

    public correClientes(){
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void run() {
        System.out.println("Cliente ingresado"+idCliente+" listo para trabajar id hilo "
                +Thread.currentThread().getId());
    }
}