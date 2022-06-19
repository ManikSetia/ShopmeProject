$(document).ready(function(){
    $("#logoutLink").on("click", function(e){
        e.preventDefault();
        document.logoutForm.submit();
    });

    customizeDropDownMenu();
});

//to make the user name on the navigation menu clickable
function customizeDropDownMenu(){

    //to make the logout button clickable
    $(".navbar .dropdown").hover(
        
        //when we move our mouse onto the text
        function(){
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
        },
        
        //when we move our mouse away from the text
        function(){
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
        }
    );

    $(".dropdown > a").click(function(){
       location.href = this.href;
    });
}