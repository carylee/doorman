(ns doorman.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as client]
            [org.httpkit.server :refer [run-server]]
            [envoy.core :refer [defenv env]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:gen-class))

(defenv :home-assistant-token
  "Long lived API token for home assistant"
  :type :string)

(defn ring-doorbell
  []
  (client/post "https://home.caryandtodd.com/api/services/media_player/play_media"
               {:headers {"Authorization" (str "Bearer " (:home-assistant-token env))}
                :form-params {"entity_id" "media_player.google_home_mini"
                              "media_content_id" "https://www.soundjay.com/door/doorbell-1.mp3"
                              "media_content_type" "audio/mp3"}
                :content-type :json}))

(defn greet
  []
  (future (ring-doorbell))
  {:status 200
   :headers {"Content-Type" "application/xml"}
   :body "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
   <Response>
     <Play>https://s3-us-west-2.amazonaws.com/www.caryme.com/doorman/come_on_up_to_202.wav</Play>
     <Play digits=\"99999\"></Play>
   </Response>"})


(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/greet" [] (greet))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8134"))]
    (run-server app {:port port})
    (println (str "Listening on port " port))))
