### `git clone`

克隆一个仓库

```bash
git clone <url>
```
### `git add`

添加文件到暂存区

```bash
# 添加一个文件
git add <file>
# 添加多个文件
git add <file1> <file2> <file3>
# 添加所有文件
git add .
```

### `git commit`

提交暂存区到本地仓库

```bash
# 提交暂存区的文件
git commit -m "commit message"
# 提交文件夹下的所有文件
git commit -a -m "commit message"
```

### `git push`

将本地仓库提交到远程仓库

```bash
# 设置过上游分支, 可以直接使用push 
git push  
# 指定分支
```

### `git reset`

将暂存区或工作区文件恢复到上一次提交状态

```bash
# 恢复暂存区 本地仓库 保留工作区
git reset HEAD~ --mixed
```