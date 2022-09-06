import static java.util.concurrent.TimeUnit.SECONDS;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.JLabel;

class Funcionamiento {
    private static int minLlegada=3,
            maxLlegada=10,
            minAtencion=4,
            maxAtencion=20;

    private static int cantBarberos=3,idCliente=1,cantClientes=4,cantSillas=10, idBarnero=1;

    public static void funcionamientoHilos() throws InterruptedException{
        ExecutorService exec = Executors.newFixedThreadPool(12);
        BarberiaFuncionamiento barberia = new BarberiaFuncionamiento(cantBarberos,cantSillas,
                                                                        minAtencion,maxAtencion);
        Random random = new Random();

        System.out.println("Barberia Abierta "+new Date());

        long tiempoInicio = System.currentTimeMillis(); //inicio del programa

        //generaar hilos barberos
        for(int i=1; i<=cantBarberos; i++){

            BarberoFuncionamiento barbero = new BarberoFuncionamiento(barberia,i);
            Thread hiloBarbero = new Thread(barbero);
            exec.execute(hiloBarbero);
            //System.out.println("Babero "+i+" listo para trabajar "+Thread.activeCount()
              //      +Thread.currentThread().getId());

        }

        //generar hilos clientes
        for(int i=1; i<=cantClientes; i++){
            ClienteFuncionamiento cliente = new ClienteFuncionamiento(barberia);
            cliente.setInicioAtencion(new Date());
            cliente.setIdClliente(idCliente++);
            Thread hiloCliente = new Thread(cliente);
            exec.execute(hiloCliente);
            //calcular tiempo de llegada
            int tiempoLlegada = ((int)Math.floor(Math.random()*(maxLlegada-minLlegada+1)+minLlegada)*(1000));

            System.out.println("Cliente "+i+" entra a la barberia "+Thread.activeCount()+
                    " "+Thread.currentThread().getId());

            System.out.println("\n Siguiente cliente llega en (segundos): "+tiempoLlegada/1000);
            Thread.sleep(tiempoLlegada); //dormir el hilo el tiempo random hasta que llega el otro cliente

        }
        exec.shutdown();
        exec.awaitTermination(12, SECONDS); //esperar a que terminen los hilos

    }

}
//separar las clases en diferentes archivos quien te conoce

class BarberoFuncionamiento implements  Runnable{
    BarberiaFuncionamiento barberia;
    private static  int identificadorBarbero;
    private static int idBarbero;
    public BarberoFuncionamiento (BarberiaFuncionamiento barberia, int idBarbero){

        this.barberia = barberia;
        this.idBarbero = idBarbero;
    }

    public int getIdentificadorBarbero() {
        return identificadorBarbero;
    }

    public void setIdentificadorBarbero(int identificadorBarbero){
        this.identificadorBarbero = identificadorBarbero;
    }
    public void run(){
        while(true){
            try{
                barberia.corte(idBarbero);
            }catch (IOException e){
                throw  new RuntimeException(e);
            }

            }
        }
    }


class ClienteFuncionamiento implements  Runnable{
    BarberiaFuncionamiento barberia;
    private static int idClliente;
    private Date inicioAtencion;
    public ClienteFuncionamiento (BarberiaFuncionamiento barberia){
        this.barberia = barberia;
    }

    public int getIdClliente(){
        return idClliente;
    }

    public Date getInicioAtencion(){
        return inicioAtencion;
    }

    public void setIdClliente(int idClliente){
        this.idClliente = idClliente;
    }

    public void setInicioAtencion(Date inicioAtencion){
        this.inicioAtencion = inicioAtencion;
    }

    private synchronized void atender(){    //synchronized para que no se atienda a dos clientes al mismo tiempo
        barberia.agregarCliente(this);
    }

    public void run(){
        atender();
    }

}

class BarberiaFuncionamiento{
    private static int minAtencion, maxAtencion, cantBarberos, cantSillas, barberosDisponibles;
    private static int atendidos = 1;

    private final AtomicInteger totalCortes = new AtomicInteger(0); //contador de cortes    //AtomicInteger para que no se repitan los id de los clientes
    private final AtomicInteger clientesPerdidos = new AtomicInteger(0);    //contador de clientes perdidos

    List<ClienteFuncionamiento> clientesLista;  //lista de clientes en espera   //LinkedList para que se pueda agregar y eliminar clientes de la lista
    Random random = new Random();
    public BarberiaFuncionamiento (int cantBarberos, int cantSillas, int minService, int maxService){
        this.cantBarberos = cantBarberos; //cantidad de barberos
        this.cantSillas = cantSillas; //sillas de espera
        this.minAtencion = minService; //minimo tiempo de servicio
        this.maxAtencion = maxService;  //maximo tiempo de servicio
        barberosDisponibles = cantBarberos;
        clientesLista = new LinkedList<ClienteFuncionamiento>();    //lista de clientes entrantes
    }
    int tiempoAtencion = ((int)Math.floor(Math.random()*(maxAtencion-minAtencion+1)+minAtencion))*(1000);

    public AtomicInteger getTotalCortes(){
        //pending
        return totalCortes;
    }

    public AtomicInteger getClientesPerdidos(){
        clientesPerdidos.get();
        //pending
        return clientesPerdidos;
    }

    public void corte(int idBarbero) throws IOException{
        ClienteFuncionamiento cliente;  //cliente que se va a atender
        synchronized (clientesLista){
            while(clientesLista.size()==0){ //no hay clientes en la lista el barbero duerme
                System.out.println("Barbero "+idBarbero+" duerme");
                //actualizar el estado del barbero a dormido interfaz consola o como verga se haga xd
                try {
                    clientesLista.wait();   //duerme el hilo porque no hay clientes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        cliente = (ClienteFuncionamiento)((LinkedList<?>)clientesLista).poll(); //saca el primer cliente de la lista
        System.out.println("Barbero "+idBarbero+" atiende a cliente "+cliente.getIdClliente());
        //actualizar el estado del barbero a atendiendo interfaz consola o como verga se haga xd

        //dormir el hilo el tiempo random de atencion


    }

    public void agregarCliente(ClienteFuncionamiento cliente ){
            System.out.println("Cliente "+cliente.getIdClliente()+" entra a la barberia a las"
            +cliente.getInicioAtencion());
            //el weon entra a la barberia
        synchronized (clientesLista){
            if(clientesLista.size() == cantSillas){ //comparar cantSillas con clientes ya en la lista(fila)
                System.out.println("Cliente "+cliente.getIdClliente()+" se va porque no hay sillas");

                clientesPerdidos.incrementAndGet(); //aumentar el contador de clientes perdidos AtomicInteger
                return;
            }else if(barberosDisponibles >0 ){ //comprobar si hay barberos disponibles
                ((LinkedList<ClienteFuncionamiento>)clientesLista).offer(cliente);
                clientesLista.notify(); //despertar el hilo del barbero
            }else{
                //si no hay barberos disponibles, pero si hay sillas el cliente se agrega a la lista (silla)
                ((LinkedList<ClienteFuncionamiento>)clientesLista).offer(cliente); //agregar al final de la lista

                System.out.println("Cliente "+cliente.getIdClliente()+" se sienta en una silla");

                if(clientesLista.size()==1){    //si el cliente es el primero en la lista, se despierta al barbero
                    clientesLista.notify(); //despertar el hilo del barbero

                }
            }

        }

    }

}

class consoleDesign {
    public void updateTotalAtendidos(int atendidos){
        System.out.println("[" + " TOTAL ATENDIDOS : " + atendidos + " ]\n");
    }
    public void ui( int minArrival, int maxArrival, int clientesCant, int maxService, int minService){

        System.out.println("[" + " Velocidad de llegada de los clientes : "+ " ]\n");
        System.out.println("[" + " Aleatorio de [ " + minArrival +" a "+maxArrival+ " ]"+" ]\n");

        System.out.println("[" + " Velocidad de atencion de los clientes : "+ " ]\n");
        System.out.println("[" + " Aleatorio de [ " + minService +" a "+maxService+ " ]"+" ]\n");

        System.out.println("[" + " Numero de sillas en la sala de espera 10: "+ " ]\n");
        System.out.println("[" + " Numero de clientes en sillas: "+clientesCant+ " ]\n");
        System.out.println("Asiento ocupado = X\n");

        for(int i=1; i<=clientesCant; i++){
            System.out.println("[ "+"x"+" ]");
        }
    }
}
class interfaz{
    public static void main(String[] args) throws  InterruptedException  {
        //jframe
        JFrame frame = new JFrame("Barberia");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
        //jlabel
        JLabel label = new JLabel();
        label.setBounds(60, 70, 100, 10);

        //jtextfield
        //JTextField textField = new JTextField("Barberia");
        //textField.setBounds(160, 170, 100, 59);
        //frame.add(textField);
        //jbutton
        //JButton button = new JButton("Click");
        //frame.add(button);

       label.setText("Barberia clientes atendidos");
        frame.add(label);

        Funcionamiento funcionamiento = new Funcionamiento();
        funcionamiento.funcionamientoHilos();
    }
}
