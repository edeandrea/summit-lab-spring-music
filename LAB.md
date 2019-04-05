```
cd summit-lab-spring-music
oc login https://master.windham-80ed.openshiftworkshop.com -u userX -p openshift
oc new-project spring-music
oc adm policy add-role-to-user view system:serviceaccount:spring-music:default
oc process -f misc/templates/postgresql-template.yml | oc create -f -
```
