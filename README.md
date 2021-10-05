# [GTI785 Systèmes d'applications mobiles]<br/>Lab 3 : JSON et REST API
## Présentation
L'objectif de ce 3<sup>ème</sup> laboratoire est d'arriver à faire communiquer l'application avec une API externe, [OpenWeather](https://openweathermap.org/api), afin d'afficher des informations sur les prévisions méteo à venir. Le TP se limite à simplement afficher les résultats d'une requête pour une prévision en temps réel localisée dans la ville choisie par l'utilisateur.

Pour ma part, le concept à été poussé plus loin en fournissant des informations plus complètes à l'aide d'un design travaillé et de nouvelles fonctionnalités pour se rapprocher du rendu d'une véritable application de météo. 

Voici une capture d'écran du résultat souhaité :

![image](https://user-images.githubusercontent.com/23462475/135953146-f5eb57d4-c38f-4e20-9732-6300f68ea5c4.png)

Et voici le rendu final :

![image](https://user-images.githubusercontent.com/23462475/135953653-6e0bb72d-1247-4854-a627-c090fbe477c7.png)

Parmis les fonctionnalités ajoutées :
- Prévision pour la journée et les 7 jours suivants.
- Rafraichissement périodique des données et à tout moment via l'icône situé dans la barre d'actions.
- Écran de paramètres accessibles via le menu de la barre d'actions permettant par exemple de changer les unités de mesures utilisées.

De plus une documentation du code est disponible à l'adresse suivante : https://krow10.github.io/Lab3-RestAPI/ 

## Installation

Récupérer la dernière version de l'apk depuis la [page de publication](https://github.com/Krow10/Lab3-RestAPI/releases) ou compilez là vous même à l'aide de Gradle !

## License

Distribué sous la license MIT. Voir le fichier [LICENSE](https://github.com/Krow10/Lab3-RestAPI/blob/master/LICENSE) pour plus d'informations.

## Contact

Etienne Donneger - etienne.donneger.1@ens.etsmtl.ca

## Remerciements

- [OpenWeather](https://openweathermap.org) - OpenWeather platform is a set of elegant and widely recognisable APIs. Powered by convolutional machine learning solutions, it is capable of delivering all the weather information necessary for decision-making for any location on the globe.
- [HelloCharts for Android](https://github.com/lecho/hellocharts-android) - Charts/graphs library for Android compatible with API 8+, several chart types with support for scaling, scrolling and animations.
- [Alexey Onufriev](https://dribbble.com/onufriev) - Free Weather Icons
