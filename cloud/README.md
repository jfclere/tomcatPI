[root@localhost tomcat-rpi3]# kubectl get deployments
NAME                  DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
tomcat-in-the-cloud   2         2         2            2           5h

kubectl expose deployment tomcat-in-the-cloud --type=NodePort --name=tomcat-in-the-cloud
then
kubectl describe services tomcat-in-the-cloud
NodePort <unset>	32206/TCP
32206 is the port...
+++
[root@localhost tomcat-rpi3]# curl http://10.0.0.204:32206/
{
  "counter": 1,
  "id": "4833B5E258B2022A600851E9AB29B8FA",
  "new": true,
  "server": "10.40.0.2",
  "hostname": "tomcat-in-the-cloud-3133120499-bks16"
}
+++

removing it:
+++
kubectl -n default delete po,svc --all
kubectl delete deployment tomcat-in-the-cloud
+++
Working nearly not sticky... (See https://kubernetes.io/docs/tasks/access-application-cluster/service-access-application-cluster/)

Problem:
+++
java.io.IOException: Server returned HTTP response code: 403 for URL: https://10.96.0.1:443/api/v1/namespaces/default/pods
	at sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1876)
+++

oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)
in kubernetes commands...
[root@localhost tomcat-rpi3]# oc project -q
error: no project has been set
+++
Name:		tomcat-in-the-cloud-3133120499-g6919
Namespace:	default
Node:		n1/10.0.0.203
Start Time:	Tue, 27 Jun 2017 09:26:50 +0200
Labels:		app=tomcat-in-the-cloud
		pod-template-hash=3133120499
Status:		Running
IP:		10.38.0.3
Controllers:	ReplicaSet/tomcat-in-the-cloud-3133120499
Containers:
  tomcat-in-the-cloud:
+++
[root@localhost tomcat-rpi3]# oc policy add-role-to-user view system:serviceaccount:tomcat-in-the-cloud:default -n tomcat-in-the-cloud
Error from server: the server could not find the requested resource (get policyBindings)
+++

kubectl create -f user.yalm
creates the user... but how to tell that the pods have to use the user...
In the /root/pi-cluster.cfg:
+++
users:
- name: kubernetes-admin
  user:
    client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM4akNDQWRxZ0F3SUJBZ0lJY0t2bG1LejRtV2N3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVU
+++
[root@localhost tomcat-rpi3]# kubectl get roles --namespace=default
NAME                  AGE
tomcat-in-the-cloud   40m
+++
+++
User used "default":
kubectl get pods/tomcat-in-the-cloud-3133120499-g6919 -o yaml
  securityContext: {}
  serviceAccount: default
  serviceAccountName: default
+++
In fact I have 2 serviceAccounts default and fabric8
+++
[root@localhost tomcat-rpi3]# kubectl get serviceaccounts/default
NAME      SECRETS   AGE
default   1         2d
+++

debugging...
kubectl attach tomcat-in-the-cloud-3133120499-7m76n -i (just get the logs...)
kubectl exec tomcat-in-the-cloud-3133120499-7m76n -- ls (works?)
kubectl exec tomcat-in-the-cloud-3133120499-7m76n -- curl -v -k https://10.96.0.1:443/api/v1/namespaces/default/pods (443)
[root@localhost tomcat-rpi3]# kubectl exec tomcat-in-the-cloud-3133120499-7m76n -- cat /var/run/secrets/kubernetes.io/serviceaccount/token
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Ii...
A token... not in /root/pi-cluster.cfg
/var/run/secrets/kubernetes.io/serviceaccount/ca.crt in the image
+++
[root@localhost tomcat-rpi3]# kubectl exec tomcat-in-the-cloud-3133120499-7m76n -- ls -l /var/run/secrets/kubernetes.io/serviceaccount
total 0
lrwxrwxrwx 1 root root 13 Jun 28 07:16 ca.crt -> ..data/ca.crt
lrwxrwxrwx 1 root root 16 Jun 28 07:16 namespace -> ..data/namespace
lrwxrwxrwx 1 root root 12 Jun 28 07:16 token -> ..data/token
+++
(token but no client certificates?
User "system:serviceaccount:default:default" cannot get namespaces in the namespace "default" (we know that :-() and the token is valid.
kubernetes-admin uses a key/cert authentication it seems :-(

In /root/pi-cluster.cfg
+++
contexts:
- context:
    cluster: kubernetes
    user: kubernetes-admin
  name: kubernetes-admin@kubernetes
current-context: kubernetes-admin@kubernetes
+++
trying:
+++
      "spec" : {
        "serviceAccountName": "kubernetes-admin",
        "serviceAccount": "kubernetes-admin",
        "containers": [
+++
fails no errors... nothing :-( user problems?

kubectl create serviceaccount tomcat-in-the-cloud (creates a user that looks OK).
https://kubernetes.io/docs/admin/authentication/
how to link the serviceaccount to the role in user.yaml still:
User "system:serviceaccount:default:tomcat-in-the-cloud" cannot get namespaces in the namespace "default".
User "system:serviceaccount:default:tomcat-in-the-cloud" cannot get namespaces in the namespace "default".: "role.rbac.authorization.k8s.io \"pod-reader\"

[root@localhost tomcat-rpi3]# kubectl exec tomcat-in-the-cloud-1234284143-mr1x5 -- curl -v -k -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InRvbWNhdC1pbi10aGUtY2xvdWQtdG9rZW4tanR3NGQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoidG9tY2F0LWluLXRoZS1jbG91ZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjY2NGVhOTNjLTViZTItMTFlNy1hNjA1LWI4MjdlYmFmZGNjNyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OnRvbWNhdC1pbi10aGUtY2xvdWQifQ.RJsHfBtvB9z7pdajMnLgeHiJ-slNzWMz98PEiQDFrDrnYpsriAnhHZsR5eQufnawB9XD9TvSe-T34urEo0IynA0qHLLKEIyMICbj9zw3zDC6iQnK-PrLoLPf5DUKrU_hq8RP0U1iRyhJjE9POABTsqaO6neogG2PjHhOlrCg2jDFE1VuuZxwFCR-xM9W6jcNEmknFiwlAoKzue95vzo7nfzvdc6nJOoNuY3Oof9Fz1rkOJ5dxgZtordF_tlPoklFMT2emiH8o_Oot3vNITghrUP3Pm98V5wE0w2qtt7ayO7ZyOb_hL4fns9RUoG3LA3w5t47GWDnndh2NUKA7KeyQw" https://10.96.0.1:443/api/v1/namespaces/default/pods

WORKS!!!!

time synchronization...

/lib/systemd/systemd-timesyncd
conf:
/etc/systemd/timesyncd.conf
