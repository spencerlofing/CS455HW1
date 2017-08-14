List of Commands:
    list-messaging nodes
        This results in information about the messaging nodes being listed
    list-weights
        This should list the link weights between the messaging nodes
    setup-overlay number-of-connections
        This sets up the overlay initially and connects each messaging node to one another, randomly
    send-overlay-link-weights
        This sets up link weights between the messaging nodes
    start number-of-rounds
        This starts a specified number of rounds of random byte arrays being sent between messaging nodes
    print-shortest-path
        Prints a matrix of shortest paths between each messaging node
    exit-overlay
        This allows a messaging node to exit the overlay

Initial Setup:
    java cs455.overlay.node.Registry portnum
    java cs455.overlay.node.MessagingNode registry-host registry-port
