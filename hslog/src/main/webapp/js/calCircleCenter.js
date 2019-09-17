function Y_Coordinates(  x,  y,  k,  x0)
{
    return k * x0 - k * x + y;
}

function calCircleCenter( p1, p2, r )
{      
    var k = 0.0,k_verticle = 0.0;
    var mid_x = 0.0,mid_y = 0.0;
    var a = 1.0;
    var b = 1.0;
    var c = 1.0;
    var center1 = new Array();
    var center2 = new Array();  


    k = (p2[1] - p1[1]) / (p2[0] - p1[0]);

    if(k == 0)
    {
        center1[0] = (p1[0] + p2[0]) / 2.0;
        center2[0] = (p1[0] + p2[0]) / 2.0;
        center1[1] = p1[1] + Math.sqrt(r * r -(p1[0] - p2[0]) * (p1[0] - p2[0]) / 4.0);
        center2[1] = p2[1] - Math.sqrt(r * r -(p1[0] - p2[0]) * (p1[0] - p2[0]) / 4.0);
    }
    else
    {
        k_verticle = -1.0 / k;
        mid_x = (p1[0] + p2[0]) / 2.0;
        mid_y = (p1[1] + p2[1]) / 2.0;
        a = 1.0 + k_verticle * k_verticle;
        b = -2 * mid_x - k_verticle * k_verticle * (p1[0] + p2[0]);
        c = mid_x * mid_x + k_verticle * k_verticle * (p1[0] + p2[0]) * (p1[0] + p2[0]) / 4.0 - 
            (r * r - ((mid_x - p1[0]) * (mid_x - p1[0]) + (mid_y - p1[1]) * (mid_y - p1[1])));
        
        center1[0] = (-1.0 * b + Math.sqrt(b * b -4 * a * c)) / (2 * a);
        center2[0] = (-1.0 * b - Math.sqrt(b * b -4 * a * c)) / (2 * a);
        center1[1] = Y_Coordinates(mid_x,mid_y,k_verticle,center1[0]);
        center2[1] = Y_Coordinates(mid_x,mid_y,k_verticle,center2[0]);
    }
	
	//return [center1[0],center1[1]]//
	return [center1[0],center1[1],center2[0],center2[1]]
    //console.log( center1[0] +  "    " + center1[1] );
    //console.log( center2[0] + "    " + center2[1] );

}