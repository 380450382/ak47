<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
        <ul class="layui-nav layui-nav-tree"  lay-filter="test">
            <li id ='menu' class="layui-nav-item layui-nav-itemed">
                <a class="" href="javascript:;">插件 plugins</a>
                <dl class="layui-nav-child">
                    <dd><a href="/plugin/control/resource">插件库</a></dd>
                    <dd><a href="/plugin/control/cache">本地已安装插件</a></dd>
                </dl>
            </li>
        </ul>
    </div>
</div>

<script>
    var left = {
        active(){
            var localUrl = window.location.pathname;
            var $ = footer.$;
            $('#menu').find('dd').each((index,item) => {
                if($(item).find('a').attr('href') == localUrl){
                $(item).addClass("layui-this")
                }
            })
        }
    }
</script>