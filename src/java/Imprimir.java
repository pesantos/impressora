/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ganon
 */
public class Imprimir extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    public static String obterDados(HttpServletRequest request){
        try{
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString().replaceAll("Â", "");
        }catch(Exception e){
            return "";
        }
        
    }
    
    
    public static String formatar(String descricao,String preco){
        
        int es = 40;
        int restantes = es-preco.length();
        
        StringBuilder sb = new StringBuilder();
        for(char ch :descricao.toCharArray()){
            if(restantes>0){
                sb.append(ch);
                restantes--;
            }
            
            
        }
        
        while(restantes>0){
            sb.append(".");
            restantes--;
        }
        
        String resultado =sb.toString()+preco;
        System.out.println(resultado);
        return resultado;
        
    }
    
    public static String testeJSON(PrintService alvo,String dados){
        String dados2 = "{'tipo':'consignacao','responsavel':'Clesio de Souza Aguiar','ambulante':'Paulo Eduardo Santos','cpfAmbulante':'016-760-806-19','evento':'Evento de Aprendizagem','dia':'14/04/2022','total':'R$ 1100,50','itens':[{'descricao':'Agua gelada','quantidade':'2(DZ)','valor':'R$ 40,00'},{'descricao':'Brahma Pilsen 350ml','quantidade':'5(DZ)','valor':'R$ 180,00'},{'descricao':'Coca-Cola Lata 350ml','quantidade':'50(UN)','valor':'R$ 340,50'},{'descricao':'Fanta Laranja Lata 350ml','quantidade':'50(UN)','valor':'R$ 340,50'}]}";
        JSONObject object = new JSONObject(dados);
        JSONArray ar = (JSONArray)object.get("itens");
        Iterator interator = ar.iterator();
        List<String> li = new LinkedList<>();
        while(interator.hasNext()){
            JSONObject linha = (JSONObject)interator.next();
            System.out.println(linha);
            li.add(Imprimir.formatar(linha.getString("descricao"),(linha.getString("quantidade")+linha.getString("valor"))));
            
        }
        System.out.println(object.get("ambulante"));
        EscPos escpos = null;
        try{
            
            PrinterOutputStream printerOutputStream = new PrinterOutputStream(alvo);
             escpos = new EscPos(printerOutputStream);
             Style title = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);
             Style ass = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);
             
             Style centralizado = new Style().setJustification(EscPosConst.Justification.Center);
             Style cbold = new Style().setJustification(EscPosConst.Justification.Center).setBold(true);

            Style subtitle = new Style(escpos.getStyle())
                    .setBold(true)
                    .setUnderline(Style.Underline.OneDotThick);
            Style bold = new Style(escpos.getStyle())
                    .setBold(true);
            Style tot = new Style(escpos.getStyle()).setBold(true).setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Right);
            EscPos temp = null;
            if(object.getString("tipo").equals("consignacao")) temp = escpos.writeLF(title,"Ticket de Consignacao");
            if(object.getString("tipo").equals("devolucao")) temp = escpos.writeLF(title,"Ticket de Devolucoes");
            if(object.getString("tipo").equals("venda")) temp = escpos.writeLF(title,"Ticket de Vendas");
                
                
                      temp.writeLF(cbold,object.getString("evento"))
                      .writeLF(cbold,object.getString("responsavel"))
                      .writeLF(cbold,object.getString("ambulante"))
                      .writeLF(cbold,object.getString("cpfAmbulante"))
                      .writeLF("----------------------------------------")
                      .writeLF(cbold,"Relacao de Produtos")
                      .feed(1);
                for(String st: li){
                    temp = temp.writeLF(st);
                }
                    temp.feed(2)
                        .writeLF(tot,"Total: "+object.getString("total"))
                        .feed(2)
                        .writeLF(centralizado,"----------------------------------------")
                        .writeLF(ass, "ASSINATURA")
                        .writeLF(title, object.getString("dia"))
                        .feed(4)
                        .cut(EscPos.CutMode.PART)
                        .feed(4);
                    
                    if(object.getString("tipo").equals("consignacao")) temp = escpos.writeLF(title,"Ticket de Consignacao");
            if(object.getString("tipo").equals("devolucao")) temp = escpos.writeLF(title,"Ticket de Devolucoes");
            if(object.getString("tipo").equals("venda")) temp = escpos.writeLF(title,"Ticket de Vendas");
                
                
                      temp.writeLF(cbold,object.getString("evento"))
                      .writeLF(cbold,object.getString("responsavel"))
                      .writeLF(cbold,object.getString("ambulante"))
                      .writeLF(cbold,object.getString("cpfAmbulante"))
                      .writeLF("----------------------------------------")
                      .writeLF(cbold,"Relacao de Produtos")
                      .feed(1);
                for(String st: li){
                    temp = temp.writeLF(st);
                }
                    temp.feed(2)
                        .writeLF(tot,"Total: "+object.getString("total"))
                        .feed(2)
                        .writeLF(centralizado,"----------------------------------------")
                        .writeLF(ass, "ASSINATURA")
                        .writeLF(title, object.getString("dia"))
                        .feed(4)
                        .cut(EscPos.CutMode.FULL);
                    
                    
                    temp.close();
                
            
            
            
        }catch(Exception e){
            try {
                escpos.close();
            } catch (IOException ex) {
                Logger.getLogger(Imprimir.class.getName()).log(Level.SEVERE, null, ex);
                return "falha";
            }
            return "falha";
        }
        
        return "sucesso";
    }
    
    
    public static PrintService encontrarImpressora(){
        DocFlavor df = DocFlavor.INPUT_STREAM.AUTOSENSE;
        
 
       
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        
        PrintService alvo = null;
        for(PrintService s:printServices){
            
            if(s.getName().equals("MP-4200 TH"))alvo = s;
            
        }
        
        return alvo;
    }
    
    public static void responderOffiline(HttpServletResponse response){
         HttpServletResponse httpResponse = (HttpServletResponse) response;
          httpResponse.addHeader("Access-Control-Allow-Origin", "*");
          httpResponse.addHeader("Access-Control-Allow-Headers","Content-Type");
         
        //or JSONArray array = new JSONArray(data); which ever the one you want
        
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("offline");
            ;
        }catch(Exception e){
            
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("Entrou no acesso");
        
         String dados = Imprimir.obterDados(request); // obtenho os dados da requisição
         System.out.println(dados);
         PrintService alvo = Imprimir.encontrarImpressora();
         if(alvo==null){
             Imprimir.responderOffiline(response);
             return;
         }
         
         String resposta = Imprimir.testeJSON(alvo, dados);
         
          HttpServletResponse httpResponse = (HttpServletResponse) response;
          httpResponse.addHeader("Access-Control-Allow-Origin", "*");
          httpResponse.addHeader("Access-Control-Allow-Headers","Content-Type");
         
        //or JSONArray array = new JSONArray(data); which ever the one you want
        
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println(resposta);
            ;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
