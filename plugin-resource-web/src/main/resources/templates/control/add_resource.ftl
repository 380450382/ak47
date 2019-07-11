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
        <label class="layui-form-label">插件入口</label>
        <div class="layui-input-block">
            <input type="text" name="description" lay-verify="required" placeholder="请输入插件入口" autocomplete="off" lay-reqtext="插件入口是必填项，岂能为空？" class="layui-input">
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
            <input type="text" name="description" lay-verify="required" placeholder="请输入版本" lay-reqtext="版本是必填项，岂能为空？" autocomplete="off" class="layui-input">
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="demo1">立即提交</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>