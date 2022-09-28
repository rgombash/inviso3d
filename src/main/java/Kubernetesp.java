import com.google.gson.*;
import com.google.gson.Gson;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;

public class Kubernetesp {
    Config CoreV1Api = null;
    public static CoreV1Api Config(String Context) throws IOException, ApiException {
        if (ProxyService.prop.getProperty("kubernetes_config_mode").equals("file_config")) {
            // file path to your KubeConfig
            String kubeConfigPath = ProxyService.prop.getProperty("kubernetes_config");
            // loading the out-of-cluster config, a kubeconfig from file-system

            KubeConfig kubeconfig = KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath));
            kubeconfig.setContext(Context);
            System.out.println(kubeconfig.getContexts());
            System.out.println(kubeconfig.getCurrentContext());

            ApiClient client = ClientBuilder.kubeconfig(kubeconfig).build();
            Configuration.setDefaultApiClient(client);

        } else if (ProxyService.prop.getProperty("kubernetes_config_mode").equals("incluster")){
            // in-cluster config
            System.out.println("incluster config init");
            // set the global default api-client to the in-cluster one from above
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            System.out.println("incluster config end");
        }
        CoreV1Api api = new CoreV1Api();
        return api;
    }
    public static String GetServices(String Context) throws IOException, ApiException {
        CoreV1Api api = Config(Context);
        //CoreV1Api api = CoreV1Api(Config.defaultClient());
        V1ServiceList list = api.listServiceForAllNamespaces(null,null,null,null,null,null,null,null,null,null);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).setPrettyPrinting().create();
        String jsonOut = gson.toJson(list.getItems());

        //System.out.println(jsonOut);

        return(jsonOut);
    }
    public static String GetNamespaces(String Context) throws IOException, ApiException {
        CoreV1Api api = Config(Context);
        //CoreV1Api api = CoreV1Api(Config.defaultClient());
        V1ServiceList list = api.listServiceForAllNamespaces(null,null,null,null,null,null,null,null,null,null);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).setPrettyPrinting().create();
        String jsonOut = gson.toJson(list.getItems());

        System.out.println(jsonOut);

        return(jsonOut);
    }

    public static String GetDeployment(String Namespace) throws IOException, ApiException {
        AppsV1Api apiInstance = new AppsV1Api(Config.defaultClient());

        V1DeploymentList list = apiInstance.listNamespacedDeployment(Namespace,null,null,null,null,null,null,null,null,null,null);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).setPrettyPrinting().create();
        String jsonOut = gson.toJson(list.getItems());

        //System.out.println(jsonOut);

        return(jsonOut);
    }

    public static String GetAllDeployments(String Context) throws IOException, ApiException {
        AppsV1Api apiInstance = new AppsV1Api(Config.defaultClient());

        V1DeploymentList list = apiInstance.listDeploymentForAllNamespaces(null,null,null,null,null,null,null,null,null,null);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).setPrettyPrinting().create();
        String jsonOut = gson.toJson(list.getItems());

        System.out.println(jsonOut);

        return(jsonOut);
    }


//    public static JSONArray GetPods(String Context) throws IOException, ApiException {
    public static String GetPods(String Context) throws IOException, ApiException {

        CoreV1Api api = Config(Context);
        JSONArray Nodes = new JSONArray();

        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).setPrettyPrinting().create();
        String jsonOut = gson.toJson(list.getItems());

        return jsonOut;
    }

    //joda datetime serializer/deserializer
    static class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return DateTime.parse(json.getAsString());
        }

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(ISODateTimeFormat
                    .dateTimeNoMillis()
                    .print(src));
        }
    }
}