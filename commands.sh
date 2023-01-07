

#Installez Memcached sur votre machine.
#Vous pouvez utiliser le paquet Memcached disponible dans la plupart des systèmes d'exploitation
#ou télécharger et compiler le code source de Memcached à partir du site Web officiel

# Démarrez le serveur Memcached :
# Le serveur démarre sur le port 11211 par défaut
memcached

# Ouvrir le serveur Memcached démarrera sur le port 11211 par défaut
telnet localhost 11211

# Envoyer des commandes Memcache utilisant Telnet
#Pour stocker une valeur en cache :
set mykey "myvalue" 3600

#Pour récupérer une valeur
get mykey

#Pour supprimer une valeur
delete mykey

#Il existe de nombreuses autres commandes pour gérer les données de manière plus avancée
#Consultez la documentation de Memcached pour en savoir plus sur les commandes disponibles.



# pour creer un cluster avec des noeuds en docker

#Créez un réseau Docker pour le cluster, exécutez la commande suivante sur l'un des serveurs :
docker network create memcached-cluster

#Lancez un conteneur Memcached sur chacun des serveurs.
docker run -d --name memcached --net memcached-cluster memcached memcached -p 11211

# Lancez un conteneur HAProxy sur l'un des serveurs
docker run -d --name haproxy --net memcached-cluster -p 11211:11211 haproxy

#Configurer HAProxy: modifier le fichier de configuration à l'intérieur du conteneur.
docker exec -it haproxy bash

#Cela ouve un shell à l'intérieur du conteneur, éditer le fichier en utilisant vi ou nano.

frontend memcached
    bind *:11211
    default_backend memcached_backend

#backend memcached_backend
#    balance roundrobin
#    option tcp-check
#    tcp-check send version\r\n
#    tcp-check expect string version
#    server server1 memcached:11211 check
#    server server2 memcached:11211 check
#    server server3 memcached:11211 check

#redémarrer le conteneur HAProxy
docker restart haproxy


