## 常用

### 查看进程内存占用

```shell
ps aux | awk '{print $6/1024 " MB\t\t" $11}' | sort -n |tail -20
```

### 查看本机外网IP

```shell
curl ip.sb
```

### 查看操作系统日志

```shell
tail -f -n 1000 /var/log/messages
```

### 清空某个文件

```shell
cat /dev/null > xxx.log
```

## docker相关

### 导出docker镜像为tgz

```shell
time docker save <image>:<tag> | gzip > <image>:<tag>.tar.gz
```

### 导入docker镜像

```shell
time docker load -i <image>:<tag>.tar.gz
```

## NFS 文件系统

### 服务状态与启动

```shell
systemctl status nfs
systemctl status rpcbind
systemctl status nfs-lock
# 重启
systemctl restart nfs
systemctl restart rpcbind
systemctl restart nfs-lock
```
