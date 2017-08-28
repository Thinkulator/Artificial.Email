 <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 
    <div data-history="false" data-role="popup" id="registerArea">
        <a href="#" data-rel="back" class="ui-btn ui-corner-all ui-shadow ui-btn-a ui-icon-delete ui-btn-icon-notext ui-btn-right">Close</a>
                    
        Sign up to use Artificial Email, speed your email testing.<br/>
        
        <form name="register" method="POST" action="<c:url value="/Registration"/>" onsubmit="return checkPass(true);">
            <script type="text/javascript">
                function checkPass(isSubmit){
                    var u = document.forms.register.user;
                    var p = document.forms.register.pass;
                    var p2 = document.forms.register.passVerify;
                    
                    if(p.value !== p2.value){
                        $('#passerror').text('The passwords do not match!').addClass('error');
                        $('#regsubmit').attr('disabled','disabled');
                        return false;
                    }else if(u.value === '' || !u.value.match(/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/)){
                        $('#passerror').text('The email address is not in a valid format').addClass('error');
                        $('#regsubmit').attr('disabled','disabled');
                        return false;
                    }else if(p.value === ''){
                        $('#passerror').text('Password is required').addClass('error');
                        $('#regsubmit').attr('disabled','disabled');
                        return false;
                    }else if(p.value.length < 8){
                        $('#passerror').text('Your password is too short').addClass('error');
                        $('#regsubmit').attr('disabled','disabled');
                        return false;
                    }else{
                        $('#passerror').text('').removeClass('error');
                        $('#regsubmit').removeAttr('disabled');
                    }
                    if(isSubmit){
                        ga('send', {
                            'hitType': 'event',
                            'eventCategory': 'navigation',
                            'eventAction': 'register'
                          });
                    }
                    return true;
                }
            </script>
            <input name="user" type="email" data-clear-btn="true" value="" placeholder="Email Address" onchange="javascript:checkPass();"><br/>

            <input name="pass" type="password" data-clear-btn="true" placeholder="Password" value="" onkeyup="javascript:checkPass();"> 
            <input name="passVerify" type="password" data-clear-btn="true" placeholder="Re-enter Password" value="" onkeyup="javascript:checkPass();">
            <div id="passerror" class="" ></div>
            
            <label for="remember" style="display:inline">Remember Me: </label><input checked style="width:100px;" name="remember" type="checkbox" data-role="flipswitch" value="yes" data-on-text="Yes" data-off-text="No" data-wrapper-class="custom-size-flipswitch"><br/>
            <br/>
            <input id="regsubmit" type="submit" value="Register">
        </form>
    </div>
