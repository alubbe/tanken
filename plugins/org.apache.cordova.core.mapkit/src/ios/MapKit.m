//
//  Cordova
//
//

#import "MapKit.h"
#import "CDVAnnotation.h"
#import "AsyncImageView.h"

@implementation MapKitView

@synthesize buttonCallback;
@synthesize childView;
@synthesize mapView;
@synthesize imageButton;


-(CDVPlugin*) initWithWebView:(UIWebView*)theWebView
{
    self = (MapKitView*)[super initWithWebView:theWebView];
    return self;
}

/**
 * Create a native map view
 */
- (void)createView
{
    NSDictionary *options = [[NSDictionary alloc] init];
    [self createViewWithOptions:options];
}

- (void)createViewWithOptions:(NSDictionary *)options {

    //This is the Designated Initializer

    // defaults
    float marginTop = ([options objectForKey:@"marginTop"]) ? [[options objectForKey:@"marginTop"] floatValue] : 200;
    float marginBottom = ([options objectForKey:@"marginBottom"]) ? [[options objectForKey:@"marginBottom"] floatValue] : 200;
    float height = self.webView.bounds.size.height - marginTop - marginBottom;
    float width = self.webView.bounds.size.width;
    float x = self.webView.bounds.origin.x;
    float y = self.webView.bounds.origin.y + marginTop;

    if ([options objectForKey:@"markerCallback"]) {
        self.buttonCallback=[[options objectForKey:@"markerCallback"] description];
    }

    self.childView = [[UIView alloc] initWithFrame:CGRectMake(x,y,width,height)];
    self.mapView = [[MKMapView alloc] initWithFrame:CGRectMake(self.childView.bounds.origin.x, self.childView.bounds.origin.x, self.childView.bounds.size.width, self.childView.bounds.size.height)];
    self.mapView.delegate = self;
    self.mapView.multipleTouchEnabled   = YES;
    self.mapView.autoresizesSubviews    = YES;
    self.mapView.userInteractionEnabled = YES;
    self.mapView.showsUserLocation = YES;
    self.mapView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.childView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;


    CLLocationCoordinate2D centerCoord = { [[options objectForKey:@"lat"] floatValue] , [[options objectForKey:@"lng"] floatValue] };
    CLLocationDistance diameter = [[options objectForKey:@"diameter"] floatValue];

    MKCoordinateRegion region=[ self.mapView regionThatFits: MKCoordinateRegionMakeWithDistance(centerCoord,
                                                                                                diameter*(height / self.webView.bounds.size.width),
                                                                                                diameter*(height / self.webView.bounds.size.width))];
    [self.mapView setRegion:region animated:YES];
    [self.childView addSubview:self.mapView];

    [ [ [ self viewController ] view ] addSubview:self.childView];

}

- (void)destroyMap:(CDVInvokedUrlCommand *)command
{
    if (self.mapView)
    {
        [ self.mapView removeAnnotations:mapView.annotations];
        [ self.mapView removeFromSuperview];

        mapView = nil;
    }
    if(self.imageButton)
    {
        [ self.imageButton removeFromSuperview];
        //[ self.imageButton removeTarget:self action:@selector(closeButton:) forControlEvents:UIControlEventTouchUpInside];
        self.imageButton = nil;

    }
    if(self.childView)
    {
        [ self.childView removeFromSuperview];
        self.childView = nil;
    }
    self.buttonCallback = nil;
}

- (void)clearMapPins:(CDVInvokedUrlCommand *)command
{
    [self.mapView removeAnnotations:self.mapView.annotations];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)addMapPins:(CDVInvokedUrlCommand *)command
{

    NSArray *pins = command.arguments[0];

  for (int y = 0; y < pins.count; y++)
    {
        NSDictionary *pinData = [pins objectAtIndex:y];
        CLLocationCoordinate2D pinCoord = { [[pinData objectForKey:@"lat"] floatValue] , [[pinData objectForKey:@"lng"] floatValue] };
        NSString *title=[[pinData valueForKey:@"title"] description];
        NSString *subTitle=[[pinData valueForKey:@"snippet"] description];
        NSInteger index=[[pinData valueForKey:@"index"] integerValue];
        BOOL selected = [[pinData valueForKey:@"selected"] boolValue];

        NSString *pinColor = nil;
        NSString *imageURL = nil;
        NSString *pinImageURL = nil;

        if([[pinData valueForKey:@"icon"] isKindOfClass:[NSNumber class]])
        {
            pinColor = [[pinData valueForKey:@"icon"] description];
        }
        else if([[pinData valueForKey:@"icon"] isKindOfClass:[NSDictionary class]])
        {
            NSDictionary *iconOptions = [pinData valueForKey:@"icon"];
            pinImageURL=[[iconOptions valueForKey:@"resource"] description];
            //pinColor = [[iconOptions valueForKey:@"pinColor" ] description];
        }
        
        if ([[pinData valueForKey:@"image"] isKindOfClass:[NSDictionary class]])
        {
            NSDictionary *iconOptions = [pinData valueForKey:@"image"];
            imageURL=[[iconOptions valueForKey:@"resource"] description];
        
        }

        CDVAnnotation *annotation = [[CDVAnnotation alloc] initWithCoordinate:pinCoord index:index title:title subTitle:subTitle imageURL:imageURL pinImageURL:pinImageURL];
        annotation.pinColor=pinColor;
        annotation.selected = selected;

        [self.mapView addAnnotation:annotation];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    }

}

-(void)showMap:(CDVInvokedUrlCommand *)command
{
    if (!self.mapView)
    {
        [self createViewWithOptions:command.arguments[0]];
    }
    self.childView.hidden = NO;
    self.mapView.showsUserLocation = YES;
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}


- (void)hideMap:(CDVInvokedUrlCommand *)command
{
    if (!self.mapView || self.childView.hidden==YES) 
    {
        return;
    }
    // disable location services, if we no longer need it.
    self.mapView.showsUserLocation = NO;
    self.childView.hidden = YES;
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)changeMapType:(CDVInvokedUrlCommand *)command
{
    if (!self.mapView || self.childView.hidden==YES)
    {
        return;
    }

    int mapType = ([command.arguments[0] objectForKey:@"mapType"]) ? [[command.arguments[0] objectForKey:@"mapType"] intValue] : 0;

    switch (mapType) {
        case 4:
            [self.mapView setMapType:MKMapTypeHybrid];
            break;
        case 2:
            [self.mapView setMapType:MKMapTypeSatellite];
            break;
        default:
            [self.mapView setMapType:MKMapTypeStandard];
            break;
    }

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

//Might need this later?
/*- (void) mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
    MKCoordinateRegion mapRegion;
    mapRegion.center = userLocation.coordinate;
    mapRegion.span.latitudeDelta = 0.2;
    mapRegion.span.longitudeDelta = 0.2;

    [self.mapView setRegion:mapRegion animated: YES];
}

- (void)mapView:(MKMapView *)theMapView regionDidChangeAnimated: (BOOL)animated
{
    NSLog(@"region did change animated");
    float currentLat = theMapView.region.center.latitude;
    float currentLon = theMapView.region.center.longitude;
    float latitudeDelta = theMapView.region.span.latitudeDelta;
    float longitudeDelta = theMapView.region.span.longitudeDelta;

    NSString* jsString = nil;
    jsString = [[NSString alloc] initWithFormat:@"geo.onMapMove(\'%f','%f','%f','%f\');", currentLat,currentLon,latitudeDelta,longitudeDelta];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    [jsString autorelease];
}
 */


- (MKAnnotationView *) mapView:(MKMapView *)theMapView viewForAnnotation:(id <MKAnnotation>) annotation {
  
  if ([annotation class] != CDVAnnotation.class) {
    return nil;
  }

    CDVAnnotation *phAnnotation=(CDVAnnotation *) annotation;
    NSString *identifier=[NSString stringWithFormat:@"INDEX[%i]", phAnnotation.index];

    MKAnnotationView *annView = (MKAnnotationView *)[theMapView dequeueReusableAnnotationViewWithIdentifier:identifier];

    if (annView!=nil) return annView;

    
    //Load Images
/*
    AsyncImageView* asyncPinImage = nil;
    asyncPinImage.tag = 999;
    if (phAnnotation.pinImageURL)
    {
        asyncPinImage = [[AsyncImageView alloc] initWithFrame:CGRectMake(0,0, 50, 32)];
        asyncPinImage.tag = 999;

        NSURL *url = [[NSURL alloc] initWithString:phAnnotation.pinImageURL];
        [asyncPinImage loadImageFromURL:url];
    }
 
*/
    UIImage *pinImage = nil;
    if (phAnnotation.pinImageURL) {
        pinImage = [UIImage imageNamed:phAnnotation.pinImageURL];
    }
    
    AsyncImageView* asyncImage = nil;
    asyncImage.tag = 999;
    if (phAnnotation.imageURL)
    {
        asyncImage = [[AsyncImageView alloc] initWithFrame:CGRectMake(0,0, 50, 32)];
        //NSURL *url = [[NSURL alloc] initWithString:phAnnotation.imageURL];
        [asyncImage loadLocalImage:phAnnotation.imageURL];
    }

    if (phAnnotation.pinColor != nil) {
        MKPinAnnotationView *annPinView;
        annPinView=[[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];

        annPinView.animatesDrop=YES;
        if ([phAnnotation.pinColor isEqualToString:@"120"])
            annPinView.pinColor = MKPinAnnotationColorGreen;
        else if ([phAnnotation.pinColor isEqualToString:@"270"])
            annPinView.pinColor = MKPinAnnotationColorPurple;
        else
            annPinView.pinColor = MKPinAnnotationColorRed;

        annView = annPinView;
    } else {
        annView = [[MKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
        if (pinImage != nil) {
            annView.image = pinImage;
        }
    }

    annView.canShowCallout = YES;
    if (asyncImage != nil) {
        annView.leftCalloutAccessoryView = asyncImage;
    }

    if (self.buttonCallback && phAnnotation.index!=-1)
    {

        UIButton *myDetailButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
        myDetailButton.frame = CGRectMake(0, 0, 23, 23);
        myDetailButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        myDetailButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
        myDetailButton.tag=phAnnotation.index;
        annView.rightCalloutAccessoryView = myDetailButton;
        [ myDetailButton addTarget:self action:@selector(checkButtonTapped:) forControlEvents:UIControlEventTouchUpInside];

    }

    if(phAnnotation.selected)
    {
        [self performSelector:@selector(openAnnotation:) withObject:phAnnotation afterDelay:1.0];
    }

    return annView;
}

-(void)openAnnotation:(id <MKAnnotation>) annotation
{
    [ self.mapView selectAnnotation:annotation animated:YES];  
    
}

- (void) checkButtonTapped:(id)button 
{
    UIButton *tmpButton = button;
    NSString* jsString = [NSString stringWithFormat:@"%@(\"%i\");", self.buttonCallback, tmpButton.tag];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)dealloc
{
    if (self.mapView)
    {
        [ self.mapView removeAnnotations:mapView.annotations];
        [ self.mapView removeFromSuperview];
        self.mapView = nil;
    }
    if(self.imageButton)
    {
        [ self.imageButton removeFromSuperview];
        self.imageButton = nil;
    }
    if(childView)
    {
        [ self.childView removeFromSuperview];
        self.childView = nil;
    }
    self.buttonCallback = nil;
}

@end
