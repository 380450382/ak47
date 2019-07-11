<div style="padding: 10px; ">
    <button type="button" class="layui-btn" id="upload"><i class="layui-icon layui-icon-upload-drag">&nbsp;</i>上传</button>
</div>
<table id="resourceTable" class="layui-table" style="margin-top: 0px;">
</table>

<div id="addResource"  hidden>
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>资源添加</legend>
    </fieldset>
    <form class="layui-form" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">别名</label>
            <div class="layui-input-block">
                <input type="text" name="name" lay-verify="required" autocomplete="off" lay-reqtext="别名是必填项，岂能为空？" placeholder="请输入别名" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">描述</label>
            <div class="layui-input-block">
                <input type="text" name="description" lay-verify="content" placeholder="请输入描述" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">上传jar包</label>
            <div class="layui-input-block">
                <button type="button" class="layui-btn" id="uploadJar"><i class="layui-icon"></i>上传文件</button>
                <input type="text" name="jar" lay-verify="required" disabled autocomplete="off" lay-reqtext="jar包，岂能为空？" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">插件入口</label>
            <div class="layui-input-block">
                <input type="text" name="className" lay-verify="required" placeholder="请输入插件入口" autocomplete="off" lay-reqtext="插件入口是必填项，岂能为空？" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">默认是否开启</label>
            <div class="layui-input-block">
                <input type="checkbox" checked="true" name="active" lay-skin="switch" lay-filter="active" lay-text="开启|禁用">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">版本</label>
            <div class="layui-input-block">
                <input type="text" name="version" lay-verify="required" placeholder="请输入版本" lay-reqtext="版本是必填项，岂能为空？" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="addResource">立即提交</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>

<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>
    layui.use(['form', 'layedit', 'laydate','table','jquery','upload'], function(){
        var $ = footer.$
        var form = layui.form
                ,layer = layui.layer
                ,layedit = layui.layedit
                ,laydate = layui.laydate
                ,upload = layui.upload;
        $("#upload").click(function () {
            layer.open({
                type: 1,
                area: ['800px', '600px'],
                title: false,
                fixed: true, //不固定
                shade:false,
                shadeClose: false, //点击遮罩关闭
                closeBtn: 1,
                content: $("#addResource")
            });
        });

        //指定允许上传的文件类型
        upload.render({
            elem: '#uploadJar'
            ,url: '/api/uploadJar'
            ,accept: 'file' //普通文件
            ,exts: 'jar' //只允许上传压缩文件
            ,done(res){
                console.log(res)
                if(res.success || res.code == 401){
                    $("[name=jar]:eq(0)").val(res.data)
                }
            }
            ,error(){
                $("[name=jar]:eq(0)").val(undefined)
            }
        });
        var table = layui.table;
        //第一个实例
        table.render({
            elem: '#resourceTable'
            // ,height: 312
            ,url: '/api/getList' //数据接口
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
            ]]
        });

        //监听指定开关
        form.on('switch(active)', function(data){
            layer.msg('开关checked：'+ (this.checked ? 'true' : 'false'), {
                offset: '6px'
            });
            layer.tips('温馨提示：请注意开关状态的文字可以随意定义，而不仅仅是ON|OFF', data.othis)
        });


        //监听提交
        form.on('submit(addResource)', function(data){
            data.field.active==="on"?data.field.active=true:data.field.active=false;
            $.ajax({
                url:"/api/upload",
                method:"post",
                data:data.field,
                dataType:"json",
                success(result){
                    if(result.success){
                        layer.msg('上传成功');
                        layer.closeAll();
                        layer.msg("上传成功")
                        table.reload('resourceTable');
                    } else {
                        layer.msg('上传失败:' + result.msg);
                        $("[name=jar]:eq(0)").val(undefined)
                    }
                },
                error(error){
                    $("[name=jar]:eq(0)").val(undefined)
                    layer.msg('上传失败,系统异常: ' + error.responseText);
                }
            })
            return false;
        });
    });
</script>