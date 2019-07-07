<table id="cache" class="layui-table" lay-filter="cache">
</table>

<script type="text/html" id="cacheBar">
    <a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="enable">启用</a>
    <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="disable">禁用</a>
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="uninstall">卸载</a>
</script>
<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>
    layui.use(['table','jquery'], function(){
        var table = layui.table;
        var $ = layui.$;
        //第一个实例
        table.render({
            id:'cacheTable',
            elem: '#cache'
            // ,height: 312
            ,url: '/api/getList?code=2' //数据接口
            ,page: true //开启分页
            ,cols: [[ //表头
                {field: 'id', title: 'ID', fixed: 'left'}
                ,{field: 'name', title: '别名'}
                ,{field: 'description', title: '描述' }
                ,{field: 'url', title: '远程url'}
                ,{field: 'jar', title: 'jar名'}
                ,{field: 'className', title: '插件入口'}
                ,{field: 'active', title: '是否启用', templet (obj) {
                    return obj.active?"启用":"禁用";
                    }}
                ,{field: 'version', title: '版本'}
                ,{field: 'count', title: '重复次数'}
                ,{field: 'expression', title: '表达式', edit: 'text', width: 450,text: 'execution(* com.ak47.plugins.service.impl..*.*(..))'}
                ,{fixed: 'right', title: '操作', toolbar: '#cacheBar',width: 200}
            ]]
        });
        //监听单元格编辑
        table.on('edit(cache)', function(obj){
            var value = obj.value //得到修改后的值
                    ,data = obj.data //得到所在行所有键值
                    ,field = obj.field; //得到字段
            layer.msg('[ID: '+ data.id +'] ' + field + ' 字段更改为：'+ value);
            obj[obj.field] = value
        });

        //监听工具条
        table.on('tool(cache)', function(obj){
            var data = obj.data;
            if(obj.event === 'enable'){
                function enable(isCover){
                    layer.msg('ID：'+ data.id + ' 正在启动，请稍等..');
                    $.ajax({
                        url:"/api/enablePlugin",
                        method:"get",
                        data:{
                            pluginsId:data.id,
                            isCover:isCover
                        },
                        dataType:"json",
                        success(result){
                            if(result.success){
                                layer.msg('ID:' + data.id + ' 启用成功!');
                                table.reload('cacheTable');
                            } else {
                                layer.msg('ID:' + data.id + ' 启用失败,失败原因: ' + result.msg);
                            }
                        },
                        error(error){
                            layer.msg('ID:' + data.id + ' 启用失败,系统异常: ' + error.responseText);
                        }
                    })
                }
                layer.confirm('是否覆盖？', {
                    btn: ['覆盖', '不覆盖','取消'] //可以无限个按钮
                    ,btn3: function(index, layero){
                        //按钮【按钮三】的回调
                        return;
                    }
                }, function(index, layero){
                    enable(true)
                }, function(index){
                    enable(false)
                });
            } else if(obj.event === 'disable'){
                function disable(isClear){
                    layer.msg('ID：'+ data.id + ' 正在禁用，请稍等..');
                    $.ajax({
                        url:"/api/disablePlugin",
                        method:"get",
                        data:{
                            pluginsId:data.id,
                            isClear:isClear
                        },
                        dataType:"json",
                        success(result){
                            if(result.success){
                                layer.msg('ID:' + data.id + ' 禁用成功!');
                                table.reload('cacheTable');
                            } else {
                                layer.msg('ID:' + data.id + ' 禁用失败,失败原因: ' + result.msg);
                            }
                        },
                        error(error){
                            layer.msg('ID:' + data.id + ' 禁用失败,系统异常: ' + error.responseText);
                        }
                    })
                }
                layer.confirm('是否清空？', {
                    btn: ['清空', '不清空','取消'] //可以无限个按钮
                    ,btn3: function(index, layero){
                        //按钮【按钮三】的回调
                        return;
                    }
                }, function(index, layero){
                    disable(true)
                }, function(index){
                    disable(false)
                });
            } else if(obj.event === 'uninstall'){
                layer.confirm('真的卸载么', function(index){
                    layer.msg('ID：'+ data.id + ' 正在禁用，请稍等..');
                    $.ajax({
                        url:"/api/uninstallPlugin",
                        method:"get",
                        data:{
                            pluginsId:data.id
                        },
                        dataType:"json",
                        success(result){
                            if(result.success){
                                layer.msg('ID:' + data.id + ' 卸载成功!');
                                table.reload('cacheTable');
                                obj.del();
                            } else {
                                layer.msg('ID:' + data.id + ' 卸载失败,失败原因: ' + result.msg);
                            }
                        },
                        error(error){
                            layer.msg('ID:' + data.id + ' 卸载失败,系统异常: ' + error.responseText);
                        }
                    })
                    layer.close(index);
                });
            }
        });
    });
</script>