//package it.richkmeli.rms.web.v1;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import it.richkmeli.jframework.auth.data.exception.AuthDatabaseException;
//import it.richkmeli.jframework.auth.web.util.AuthServletManager;
//import it.richkmeli.jframework.crypto.Crypto;
//import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
//import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
//import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
//import it.richkmeli.rms.data.entity.rmc.model.Rmc;
//import it.richkmeli.rms.web.util.RMSServletManager;
//import it.richkmeli.rms.web.util.RMSSession;
//import it.richkmeli.rms.web.util.RMSStatusCode;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@WebServlet(
//        name = "rmc",
//        description = "",
//        urlPatterns = {"/rmc", "/Richkware-Manager-Server/rmc"}
//)
//public class rmc extends HttpServlet {
//
//    /**
//     * GET
//     *
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
//       try {
//            RMSServletManager rmsServletManager = new RMSServletManager(request,response);
//            Map<String, String> attribMap = rmsServletManager.doDefaultProcessRequest();
//            rmsServletManager.checkLogin();
//            RMSSession rmsSession = rmsServletManager.getRMSServerSession();
//
//           List<Rmc> clients;
//           if (rmsSession.isAdmin()) {
//                //ottiene tutti i client presenti sul db
//                //Logger.info("Admin user");
//                clients = rmsSession.getRmcDatabaseManager().getAllRMCs();
//            } else {
//                //ottiene tutti i client associati al suo account
//                //Logger.info("Regular user");
//                clients = rmsSession.getRmcDatabaseManager().getRMCs(rmsSession.getUserID());
//            }
//
//            String clientsFormatted = generateRmcListJSON(clients);
//            //Logger.info("rmc: clientsFormatted: " + clientsFormatted);
//            String output = rmsServletManager.doDefaultProcessResponse(clientsFormatted);
//            AuthServletManager.print(response, new OkResponse(RMSStatusCode.SUCCESS, output));
//
//
//        } catch (JServletException e) {
//            AuthServletManager.print(response, e.getResponse());
//        } catch (AuthDatabaseException e) {
//            AuthServletManager.print(response, new KoResponse(RMSStatusCode.DB_ERROR, e.getMessage()));
//        } catch (Exception e){
//            //e.printStackTrace();
//            AuthServletManager.print(response, new KoResponse(RMSStatusCode.GENERIC_ERROR, e.getMessage()));
//        }
//    }
//
//    /**
//     * DELETE
//     *
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
////        super.doDelete(request, response);
//        try {
//            RMSServletManager rmsServletManager = new RMSServletManager(request,response);
//            RMSSession rmsSession = rmsServletManager.getRMSServerSession();
//
//            // _TODO controlla che stia cancellando un rmc di cui ha i permessi
//            if (request.getParameterMap().containsKey("associatedUser") && request.getParameterMap().containsKey("rmcId")) {
//                String associatedUser = request.getParameter("associatedUser");
//                String rmcId = request.getParameter("rmcId");
//                boolean valid = true;
//                if (!rmsSession.isAdmin()) {
//                    if (rmsSession.getUserID().equals(associatedUser)) {
//                        Rmc temp = new Rmc(associatedUser, rmcId);
//                        if (!rmsSession.getRmcDatabaseManager().getRMCs(associatedUser).contains(temp)) {
//                            valid = false;
//                        }
//                    } else {
//                        AuthServletManager.print(response, new KoResponse(RMSStatusCode.GENERIC_ERROR, "You are not allowed to delete this state."));
//                    }
//                }
//                if (valid) {
//                    File secureDataServer = new File("secureDataServer.txt");
//                    String serverKey = "keyServer";
//                    Crypto.Server cryptoServer = new Crypto.Server();
//                    cryptoServer.init(secureDataServer, serverKey, rmcId, "");
//                    cryptoServer.deleteClientData();
//
//                    rmsSession.getRmcDatabaseManager().removeRMC(rmcId);
//                }
//
//                AuthServletManager.print(response, new OkResponse(RMSStatusCode.SUCCESS,"RMC "+rmcId+" removed."));
//            } else {
//                // _TODO errore: user does not match
//                AuthServletManager.print(response, new KoResponse(RMSStatusCode.GENERIC_ERROR, "Error in parameters passed."));
//            }
//
//            // _TODO cancella utente specifico, decidi se farlo solo da autenticato, magari con email o altro fattore di auth
////            session.getCryptoServer().deleteClientData();
//
//
//        } catch (JServletException e) {
//            AuthServletManager.print(response, e.getResponse());
//        } catch (AuthDatabaseException e1) {
//            AuthServletManager.print(response, new KoResponse(RMSStatusCode.DB_ERROR, e1.getMessage()));
//        }
//
//    }
//
//    private String generateRmcListJSON(List<Rmc> clients) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String rmcListJSON = objectMapper.writeValueAsString(clients);
//        return rmcListJSON;
//    }
//}
