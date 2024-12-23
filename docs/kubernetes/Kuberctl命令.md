## POD

### 容器交互式shell

```shell
kubectl exec -i -t -n <namespace> <pod> "--" sh -c "clear; (bash || ash || sh)"
# 多个容器需指定容器名称
kubectl exec -i -t -n <namespace> <pod> -c <container> "--" sh -c "clear; (bash || ash || sh)"
```

### 日志

```shell
# <line>: 日志行数
kubectl logs -f --tail=<line> <pod> -n <namespace>
kubectl logs -f --tail=<line> $(kubectl get pods -n <namespace> |grep <pod> |awk '{print $1}') -n <namespace>
```

### 清理Evicted的Pod

```shell
# 删除所有namespace的Evicted Pod
eval $(kubectl get pods -A | grep Evicted | awk '{print "kubectl delete pods -n "$1,$2";"}')
# 删除某namespace的Evicted Pod
kubectl delete pod $(kubectl get pods -n <namespace> |grep Evicted | awk '{print $1}') -n <namespace>
```

