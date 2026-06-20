package controlador;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import java.util.List;
import modelo.Departamento;
import modelo.Empleado;
import org.primefaces.model.chart.PieChartModel;

@Named("graficaController")
@RequestScoped
public class GraficaController implements Serializable {

    private DepartamentoFacade departamentoFacade = new DepartamentoFacade();
    
    private EmpleadoFacade empleadoFacade = new EmpleadoFacade();

    private PieChartModel pieModel;

    @PostConstruct
    public void init() {
        createPieModel();
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    private void createPieModel() {
        pieModel = new PieChartModel();
        List<Departamento> departamentos = departamentoFacade.findAll();
        List<Empleado> empleados = empleadoFacade.findAll();

        for (Departamento d : departamentos) {
            long count = empleados.stream()
                .filter(e -> e.getIdDepartamento() != null && e.getIdDepartamento().equals(d))
                .count();
            pieModel.set(d.getNombreDepartamento(), count);
        }
        
        pieModel.setTitle("Empleados por Departamento");
        pieModel.setLegendPosition("w");
        pieModel.setShowDataLabels(true);
    }
}
