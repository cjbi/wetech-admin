package tech.wetech.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.wetech.admin.model.dto.ResourceDTO;
import tech.wetech.admin.service.ResourceService;
import tech.wetech.admin.service.UserService;
import tech.wetech.admin.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cjbi
 */
//@Controller
@Slf4j
public class HomeController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String showLoginForm(HttpServletRequest req, Model model) {
        String exceptionClassName = (String) req.getAttribute("shiroLoginFailure");
        log.info("begin to login");
        String error = null;
        if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if (ExcessiveAttemptsException.class.getName().equals(exceptionClassName)) {
            error = "登陆失败次数过多";
        } else if (exceptionClassName != null) {
            error = "其他错误：" + exceptionClassName;
        }
        model.addAttribute("error", error);
        return "system/login";
    }

//    @GetMapping("/")
//    public String index(Model model) {
//        String username = (String) SecurityUtils.getSubject().getPrincipal();
//        Set<String> permissions = userService.findPermissions(username);
//        List<ResourceDTO> menus = resourceService.findMenus(permissions);
//        StringBuilder dom = new StringBuilder();
//        getMenuTree(menus, Constants.MENU_ROOT_ID, dom);
//        model.addAttribute(Constants.MENU_TREE, dom);
//        return "base/main";
//    }

    private List<ResourceDTO> getMenuTree(List<ResourceDTO> source, Long pid, StringBuilder dom) {
        List<ResourceDTO> target = getChildResourceByPId(source, pid);
        target.forEach(res -> {
            dom.append("<li class='treeview'>");
            dom.append("<a href='" + res.getUrl() + "'>");
            dom.append("<i class='" + res.getIcon() + "'></i>");
            dom.append("<span>" + res.getName() + "</span>");
            if (Constants.SHARP.equals(res.getUrl())) {
                dom.append("<span class='pull-right-container'><i class='fa fa-angle-left pull-right'></i> </span>");
            }
            dom.append("</a>");
            dom.append("<ul class='treeview-menu'>");
            res.setChildren(getMenuTree(source, res.getId(), dom));
            dom.append("</ul>");
            dom.append("</li>");
        });
        return target;
    }

    private List<ResourceDTO> getChildResourceByPId(List<ResourceDTO> source, Long pId) {
        List<ResourceDTO> child = new ArrayList<>();
        source.forEach(res -> {
            if (pId.equals(res.getParentId())) {
                child.add(res);
            }
        });
        return child;
    }
}
