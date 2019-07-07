<table id="resource" class="layui-table" lay-filter="resource">
</table>

<script type="text/html" id="resourceBar">
    <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="install">安装</a>
</script>

<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>
    layui.use(['table','jquery'], function(){
        var $ = layui.$
        var table = layui.table;
        //第一个实例
        table.render({
            elem: '#resource'
            // ,height: 312
            ,url: '/api/getList?code=4' //数据接口
            ,page: true //开启分页
            ,cols: [[ //表头
                {field: 'id', title: 'ID', fixed: 'left'}
                ,{field: 'name', title: '别名' }
                ,{field: 'description', title: '描述' }
                ,{field: 'url', title: '远程url' }
                ,{field: 'jar', title: 'jar名' }
                ,{field: 'className', title: '插件入口' }
                ,{field: 'active', title: '默认是否开启', templet (obj) {
                        return obj.active?"启用":"禁用";
                    }}
                ,{field: 'version', title: '版本' }
                ,{field: 'expression', title: '表达式', edit: 'text', width: 450}
                ,{fixed: 'right', title: '操作', toolbar: '#resourceBar'}
            ]]
        });
        //监听单元格编辑
        table.on('edit(resource)', function(obj){
            var value = obj.value //得到修改后的值
                    ,data = obj.data //得到所在行所有键值
                    ,field = obj.field; //得到字段
            layer.msg('[ID: '+ data.id +'] ' + field + ' 字段更改为：'+ value);
            obj[obj.field] = value
        });

        //监听工具条
        table.on('tool(resource)', function(obj){
            var data = obj.data;
            if(obj.event === 'install'){
                layer.msg('ID：'+ data.id + ' 正在安装，请稍等..');
                $.ajax({
                    url:"/api/installPlugin",
                    method:"get",
                    data:{
                        pluginsId:data.id,
                        expression:data.expression
                    },
                    dataType:"json",
                    success(result){
                        if(result.success){
                            layer.msg('ID:' + data.id + ' 安装成功!');
                        } else {
                            layer.msg('ID:' + data.id + ' 安装失败,失败原因: ' + result.msg);
                        }
                    },
                    error(error){
                        layer.msg('ID:' + data.id + ' 安装失败,系统异常: ' + error.responseText);
                    }
                })
            }
        });
    });
</script>