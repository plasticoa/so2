import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
public class Corre {
    private static int minLlegada=3,
            maxLlegada=10,
            minAtencion=4,
            maxAtencion=20;
    public static void corre() throws InterruptedException {
    interfazProyecto interfaz = new interfazProyecto();

    int cantidadBarberos= 3, idCliente=1, cantidadClientes=25, cantidadSillas=10;

    barberia barberia = new barberia(cantidadBarberos,cantidadSillas,
            minAtencion,maxAtencion);   //crea la barberia

        //generar hilos de los barberos
        for(int i=1; i<=cantidadBarberos; i++) {
            correBarbero obj = new correBarbero(barberia,i);    //crea el barbero
            Thread thread = new Thread(obj);    //crea el hilo del barbero
            thread.start();     //inicia el hilo del barbero
        }
        for(int i=1; i<=cantidadClientes; i++){
            correClientes obj = new correClientes(barberia);    //crea el cliente
            obj.setIdCliente(idCliente++);  //le asigna un id
            obj.setInicioEspera(new Date());    //le asigna la hora de inicio de espera
            Thread hiloCliente = new Thread(obj);   //crea el hilo del cliente
            hiloCliente.start();    //inicia el hilo del cliente
            //calcular tiempo de llegada
            int tiempoLlegada = ((int)Math.floor(Math.random()*(maxLlegada-minLlegada+1)+minLlegada)*(1000));
            interfaz.updateLlegada(minLlegada,maxLlegada,tiempoLlegada); //actualiza la interfaz tiempos de llegada nuevo cliente
            System.out.println("\n Siguiente cliente llega en (segundos): "+tiempoLlegada/1000);
            interfaz.agregarElementoListaCliente("Siguiente cliente llega en (segundos): "+tiempoLlegada/1000);
            Thread.sleep(1000); //dormir el hilo el tiempo random hasta que llega el otro cliente
        }


        }
}
class correBarbero implements  Runnable{
    interfazProyecto interfaz = new interfazProyecto();
    barberia barberia; //barberia
    int idBarbero;
    public correBarbero(barberia barberia,int idBarbero){
        this.idBarbero = idBarbero;
        this.barberia = barberia;
    }
    public void run() {
        interfaz.agregarElementoListaBarbero("Hilo de barbero creado"+Thread.currentThread().getId());
        System.out.println("Hilo de barbero creado"+Thread.currentThread().getId());
        while(true){
            try{
                barberia.corte(idBarbero);
            }catch (IOException e){
                throw  new RuntimeException(e);
            }

        }
    }
}

class correClientes implements Runnable{
    barberia barberia;
    int idCliente;
    Date inicioEspera;

    public correClientes(barberia barberia){
        this.barberia = barberia;
    }

    public int getIdCliente() {
        return idCliente;
    }
    public Date getInicioEspera() {
        return inicioEspera;
    }
    

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;      //setear id del cliente
    }
    public void setInicioEspera(Date inicioEspera) {
        this.inicioEspera = inicioEspera;       //setear la hora de inicio de espera
    }
    
    private  synchronized void espera(){
        barberia.agregarCliente(this);
    }
    public void run() {
        System.out.println( "Hilo de cliente creado "
                +Thread.currentThread().getId());
        espera();
    }
}

class barberia{
    //interfaz de la barberia
    interfazProyecto interfaz = new interfazProyecto();
    private static int minAtencion, maxAtencion, cantBarberos, cantSillas, barberosDisponibles;
    private static int atendidos = 1;

    private final AtomicInteger totalCortes = new AtomicInteger(0); //contador de cortes    //AtomicInteger para que no se repitan los id de los clientes
    private final AtomicInteger clientesPerdidos = new AtomicInteger(0);    //contador de clientes perdidos

    List<correClientes> clientesLista;  //lista de clientes en espera   //LinkedList para que se pueda agregar y eliminar clientes de la lista
    Random random = new Random();

    public barberia (int cantBarberos, int cantSillas, int minService, int maxService){
        this.cantBarberos = cantBarberos; //cantidad de barberos
        this.cantSillas = cantSillas; //sillas de espera
        this.minAtencion = minService; //minimo tiempo de servicio
        this.maxAtencion = maxService;  //maximo tiempo de servicio
        barberosDisponibles = cantBarberos; //barberos disponibles
        clientesLista = new LinkedList<correClientes>();    //lista de clientes entrantes
    }


    public AtomicInteger getTotalCortes(){
        //pending
        return totalCortes;
    }

    public AtomicInteger getClientesPerdidos(){
        clientesPerdidos.get();
        //pending
        return clientesPerdidos;
    }
    public void corte(int idBarbero) throws IOException {
        //meter clientes a la lista sino se queda tieso unu 
        correClientes cliente;  //cliente que se va a atender
        synchronized (clientesLista){
            while(clientesLista.size()==0){ //no hay clientes en la lista el barbero duerme
                interfaz.estadoLabelBarbero("Dormido",idBarbero); //actualiza la interfaz estado del barbero
                interfaz.estadoImagenBarbero(false,idBarbero); //actualiza la interfaz imagen del barbero
                interfaz.estadoSilla(0); //actualizar la interfaz de sillas

                System.out.println("Barbero "+idBarbero+" duerme");
                interfaz.agregarElementoListaBarbero("Barbero "+idBarbero+" duerme");

                //actualizar el estado del barbero a dormido interfaz consola o como verga se haga xd
                try {
                    clientesLista.wait();   //duerme el hilo porque no hay clientes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        cliente = (correClientes)((LinkedList<?>)clientesLista).poll(); //saca el primer cliente de la lista
        System.out.println("Barbero "+idBarbero+" atiende a cliente "+cliente.getIdCliente());
        interfaz.agregarElementoListaBarbero("Barbero "+idBarbero+" atiende a cliente "+cliente.getIdCliente());
        //actualizar el estado del barbero a atendiendo interfaz consola o como verga se haga xd

        //dormir el hilo el tiempo random de atencion
        try {
            barberosDisponibles--;  //barbero ocupado
            int tiempoAtencion = ((int)Math.floor(Math.random()*(maxAtencion-minAtencion+1)+minAtencion))*(1000);
            interfaz.updateAtencion(minAtencion,maxAtencion,tiempoAtencion); //actualiza la interfaz tiempos de atencion
            interfaz.estadoLabelBarbero("Despierto",idBarbero); //actualiza la interfaz estado del barbero
            interfaz.estadoImagenBarbero(true,idBarbero); //actualiza la interfaz imagen del barbero
            System.out.println("Barbero"+idBarbero+" corte al cliente "+cliente.getIdCliente()
                    +" tiempo de atencion"+tiempoAtencion/1000);

            interfaz.agregarElementoListaBarbero("Barbero"+idBarbero+" corte al cliente "+cliente.getIdCliente() +" tiempo de atencion"+tiempoAtencion/1000);
           Thread.sleep(tiempoAtencion);

            //temporizador de espera
            Date finEspera = new Date();
            long tiempoEspera = finEspera.getTime() - cliente.getInicioEspera().getTime();
            System.out.println("Tiempo de espera del cliente "+cliente.getIdCliente()+" fue de "+tiempoEspera/1000+" segundos");
            interfaz.agregarElementoListaCliente("Tiempo de espera del cliente "+cliente.getIdCliente()+" fue de "+tiempoEspera/1000+" segundos");

            System.out.println("\nCorte completado del cliente "+cliente.getIdCliente()
                    +" por el barbero "+idBarbero+" en"+tiempoAtencion/1000+" segundos"+"hora de salida"+finEspera);
            interfaz.agregarElementoListaCliente("Corte completado del cliente "+cliente.getIdCliente()+" por el barbero "+idBarbero+" en"+tiempoAtencion/1000+" segundos"+"hora de salida"+finEspera);

            totalCortes.incrementAndGet();  //incrementar el contador de cortes

            interfaz.updateAtendidos(getTotalCortes().get()); //actualizar el contador de cortes en la interfaz

            if (clientesLista.size()>0){    //si hay clientes en la lista el barbero lo despierto sino se duerme
                System.out.println("Barbero "+idBarbero+" despierta a cliente "+cliente.getIdCliente());
                interfaz.agregarElementoListaBarbero("Barbero "+idBarbero+" despierta a cliente "+cliente.getIdCliente());
            }
            barberosDisponibles++;  //barbero disponible
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void agregarCliente(correClientes cliente ){
        System.out.println("Cliente "+cliente.getIdCliente()+" entra a la barberia a las"
                +cliente.getInicioEspera());
        interfaz.agregarElementoListaCliente("Cliente "+cliente.getIdCliente()+" entra a la barberia a las"
                +cliente.getInicioEspera());
        //el weon entra a la barberia
        synchronized (clientesLista){
            if(clientesLista.size() == cantSillas){ //comparar cantSillas con clientes ya en la lista(fila)
                System.out.println("Cliente "+cliente.getIdCliente()+" se va porque no hay sillas");
                clientesPerdidos.incrementAndGet(); //aumentar el contador de clientes perdidos AtomicInteger
                interfaz.setLabelClientesPerididos(getClientesPerdidos().get()); //actualiza la interfaz clientes perdidos
                return;
            }else if(barberosDisponibles >0 ){ //comprobar si hay barberos disponibles
                ((LinkedList<correClientes>)clientesLista).offer(cliente);
                clientesLista.notify(); //despertar el hilo del barbero
            }else{
                //si no hay barberos disponibles, pero si hay sillas el cliente se agrega a la lista (silla)
                ((LinkedList<correClientes>)clientesLista).offer(cliente); //agregar al final de la lista

                System.out.println("Cliente "+cliente.getIdCliente()+" se sienta en una silla");
                //tamaño de la lista de clientes
                System.out.println("Tamaño de la lista de clientes "+clientesLista.size());
                int sillasEspera = clientesLista.size();
                interfaz.estadoSilla(sillasEspera); //actualizar la interfaz de sillas

                if(clientesLista.size()==1){    //si el cliente es el primero en la lista, se despierta al barbero
                    clientesLista.notify(); //despertar el hilo del barbero

                }
            }

        }

    }
}